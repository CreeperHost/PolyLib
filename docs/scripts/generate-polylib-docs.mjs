import { mkdir, readdir, readFile, rm, writeFile } from 'node:fs/promises';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const docsDir = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const repoRoot = path.resolve(docsDir, '..');
const sourceRepoRoot = process.env.POLYLIB_SOURCE_DIR
  ? path.resolve(process.env.POLYLIB_SOURCE_DIR)
  : repoRoot;
const generatedRoot = path.join(docsDir, 'src/content/docs/reference/generated');
const publicRoot = path.join(docsDir, 'public');

const sourceRoots = [
  { loader: 'common', dir: path.join(sourceRepoRoot, 'common/src/main/java') },
  { loader: 'fabric', dir: path.join(sourceRepoRoot, 'fabric/src/main/java') },
  { loader: 'neoforge', dir: path.join(sourceRepoRoot, 'neoforge/src/main/java') },
];

const gradleProperties = await readProperties(path.join(sourceRepoRoot, 'gradle.properties'));
const releaseTag = process.env.GITHUB_REF_NAME || `${gradleProperties.minecraft_version}-${gradleProperties.version}`;

await rm(generatedRoot, { recursive: true, force: true });
await mkdir(generatedRoot, { recursive: true });
await mkdir(publicRoot, { recursive: true });

const javaFiles = [];
for (const sourceRoot of sourceRoots) {
  const files = await listFiles(sourceRoot.dir, '.java');
  for (const file of files) javaFiles.push({ ...sourceRoot, file });
}

const classes = [];
const events = [];
const packages = new Map();

for (const source of javaFiles) {
  const text = await readFile(source.file, 'utf8');
  const parsed = parseJavaFile(text, source);
  for (const type of parsed.types) {
    classes.push(type);
    const packageEntry = ensurePackage(packages, type.packageName);
    packageEntry.loaders.add(type.loader);
    packageEntry.classes.push(type);
  }
  for (const event of parsed.events) events.push(event);
}

classes.sort((a, b) => a.qualifiedName.localeCompare(b.qualifiedName));
events.sort((a, b) => a.qualifiedName.localeCompare(b.qualifiedName));

await writePackagePages(packages);
await writeClassPages(classes);
await writeEventPages(events);
await writeApiIndex({ classes, events, packages });
await writeLlmFiles({ classes, events, packages });

console.log(`Generated ${classes.length} type pages, ${events.length} event entries, and ${packages.size} package pages for ${releaseTag}.`);

async function readProperties(file) {
  const text = await readFile(file, 'utf8');
  const result = {};
  for (const line of text.split(/\r?\n/)) {
    const trimmed = line.trim();
    if (!trimmed || trimmed.startsWith('#')) continue;
    const index = trimmed.indexOf('=');
    if (index === -1) continue;
    result[trimmed.slice(0, index).trim()] = trimmed.slice(index + 1).trim();
  }
  return result;
}

async function listFiles(dir, extension) {
  try {
    const entries = await readdir(dir, { withFileTypes: true });
    const files = await Promise.all(entries.map(async (entry) => {
      const fullPath = path.join(dir, entry.name);
      if (entry.isDirectory()) return listFiles(fullPath, extension);
      return entry.isFile() && entry.name.endsWith(extension) ? [fullPath] : [];
    }));
    return files.flat();
  } catch {
    return [];
  }
}

function parseJavaFile(text, source) {
  const packageName = text.match(/^\s*package\s+([\w.]+);/m)?.[1] || '';
  if (!packageName) return { types: [], events: [] };
  const imports = [...text.matchAll(/^\s*import\s+([\w.*]+);/gm)].map((match) => match[1]);
  const types = [];
  const events = [];
  const typePattern = /((?:\/\*\*[\s\S]*?\*\/\s*)?)(?:@\w+(?:\([^)]*\))?\s*)*public\s+(?:abstract\s+|final\s+|sealed\s+|non-sealed\s+)?(class|interface|enum|record)\s+([A-Za-z_$][\w$]*)[^{;]*/g;
  const sourceRelativePath = path.relative(sourceRepoRoot, source.file).replace(/\\/g, '/');

  for (const match of text.matchAll(typePattern)) {
    if (braceDepthAt(text, match.index) !== 0) continue;
    const declaration = match[0].slice(match[1].length).replace(/\s+/g, ' ').trim();
    const name = match[3];
    const qualifiedName = packageName ? `${packageName}.${name}` : name;
    const body = extractTypeBody(text, match.index + match[0].length);
    const javadoc = cleanJavadoc(match[1]);
    const methods = parseMethods(body, name);
    const fields = parseFields(body);
    const type = {
      name,
      kind: match[2],
      qualifiedName,
      packageName,
      loader: source.loader,
      sourcePath: sourceRelativePath,
      declaration,
      javadoc,
      imports,
      methods,
      fields,
    };
    types.push(type);

    for (const event of parseEvents(body, type)) events.push(event);
  }

  return { types, events };
}

function braceDepthAt(text, endIndex) {
  let depth = 0;
  let inString = false;
  let inChar = false;
  let inLineComment = false;
  let inBlockComment = false;

  for (let index = 0; index < endIndex; index += 1) {
    const char = text[index];
    const next = text[index + 1];

    if (inLineComment) {
      if (char === '\n') inLineComment = false;
      continue;
    }
    if (inBlockComment) {
      if (char === '*' && next === '/') {
        inBlockComment = false;
        index += 1;
      }
      continue;
    }
    if (inString) {
      if (char === '\\') {
        index += 1;
      } else if (char === '"') {
        inString = false;
      }
      continue;
    }
    if (inChar) {
      if (char === '\\') {
        index += 1;
      } else if (char === "'") {
        inChar = false;
      }
      continue;
    }

    if (char === '/' && next === '/') {
      inLineComment = true;
      index += 1;
    } else if (char === '/' && next === '*') {
      inBlockComment = true;
      index += 1;
    } else if (char === '"') {
      inString = true;
    } else if (char === "'") {
      inChar = true;
    } else if (char === '{') {
      depth += 1;
    } else if (char === '}') {
      depth -= 1;
    }
  }

  return depth;
}

function extractTypeBody(text, startIndex) {
  const open = text.indexOf('{', startIndex);
  if (open === -1) return '';
  let depth = 0;
  for (let index = open; index < text.length; index += 1) {
    if (text[index] === '{') depth += 1;
    if (text[index] === '}') depth -= 1;
    if (depth === 0) return text.slice(open + 1, index);
  }
  return text.slice(open + 1);
}

function parseMethods(body, typeName) {
  const methods = [];
  const methodPattern = /(?:@\w+(?:\([^)]*\))?\s*)*(public|protected)\s+(?:static\s+|final\s+|abstract\s+|default\s+|synchronized\s+|native\s+|strictfp\s+)*([A-Za-z_$][\w$<>\[\].?,\s&]*\s+)?([A-Za-z_$][\w$]*)\s*\(([^)]*)\)\s*(?:throws\s+[^{;]+)?[;{]/g;
  for (const match of body.matchAll(methodPattern)) {
    const name = match[3];
    if (name === 'if' || name === 'for' || name === 'while' || name === 'switch' || name === 'catch') continue;
    const returnType = name === typeName ? '' : (match[2] || '').trim();
    methods.push({
      name,
      visibility: match[1],
      returnType,
      parameters: normalizeWhitespace(match[4]),
      signature: `${match[1]} ${returnType ? `${returnType} ` : ''}${name}(${normalizeWhitespace(match[4])})`.trim(),
      javadoc: findAttachedJavadoc(body, match.index),
    });
  }
  return uniqueBy(methods, (method) => method.signature);
}

function parseFields(body) {
  const fields = [];
  const fieldPattern = /(?:@\w+(?:\([^)]*\))?\s*)*public\s+(?:static\s+|final\s+|volatile\s+|transient\s+)*([A-Za-z_$][\w$<>\[\].?,\s&]*)\s+([A-Z_a-z$][\w$]*)\s*(?:=|;)/g;
  for (const match of body.matchAll(fieldPattern)) {
    fields.push({
      name: match[2],
      type: normalizeWhitespace(match[1]),
      javadoc: findAttachedJavadoc(body, match.index),
    });
  }
  return uniqueBy(fields, (field) => `${field.type} ${field.name}`);
}

function parseEvents(body, type) {
  const result = [];
  const eventPattern = /public\s+static\s+final\s+PolyEvent<([A-Za-z_$][\w$]*)>\s+([A-Z][A-Z0-9_]*)\s*=/g;
  for (const match of body.matchAll(eventPattern)) {
    const handler = match[1];
    const handlerMethod = findHandlerMethod(body, handler);
    const javadoc = findAttachedJavadoc(body, match.index);
    result.push({
      name: match[2],
      handler,
      group: type.name,
      packageName: type.packageName,
      loader: type.loader,
      qualifiedName: `${type.qualifiedName}.${match[2]}`,
      declaringType: type.qualifiedName,
      sourcePath: type.sourcePath,
      javadoc,
      method: handlerMethod,
      cancellable: isCancellableEvent(javadoc, handlerMethod),
    });
  }
  return result;
}

function findHandlerMethod(body, handler) {
  const interfacePattern = new RegExp(`public\\s+interface\\s+${escapeRegex(handler)}\\s*\\{([\\s\\S]*?)\\n\\s*\\}`, 'm');
  const interfaceMatch = body.match(interfacePattern);
  if (!interfaceMatch) return null;
  const methodMatch = interfaceMatch[1].match(/(?:^|\n)\s*(?:\/\*\*[\s\S]*?\*\/\s*)?(?:@\w+(?:\([^)]*\))?\s*)*(?:(?:public|default)\s+)?([A-Za-z_$][\w$<>\[\].?, &]*)\s+([A-Za-z_$][\w$]*)\s*\(([^)]*)\)\s*(?:throws\s+[^{;]+)?[;{]/m);
  if (!methodMatch) return null;
  const returnType = normalizeWhitespace(methodMatch[1]);
  return {
    name: methodMatch[2],
    returnType,
    parameters: normalizeWhitespace(methodMatch[3]),
    signature: `${returnType} ${methodMatch[2]}(${normalizeWhitespace(methodMatch[3])})`,
  };
}

function isCancellableEvent(javadoc, handlerMethod) {
  const signature = handlerMethod?.signature || '';
  return handlerMethod?.returnType === 'boolean'
    || /CancelContext|ctx\)|ctx,/.test(signature)
    || /\b(cancel|suppress|prevent|return false)\b/i.test(javadoc);
}

function findAttachedJavadoc(body, index) {
  const before = body.slice(0, index);
  const close = before.lastIndexOf('*/');
  if (close === -1) return '';

  const tail = before.slice(close + 2);
  if (!/^(?:\s|@\w+(?:\([^)]*\))?)*$/.test(tail)) return '';

  const open = before.lastIndexOf('/**', close);
  if (open === -1) return '';

  return cleanJavadoc(before.slice(open, close + 2));
}

function cleanJavadoc(raw) {
  if (!raw) return '';
  const body = raw
    .replace(/^\s*\/\*\*\s?/, '')
    .replace(/\s*\*\/\s*$/, '')
    .split(/\r?\n/)
    .map((line) => line.replace(/^\s*\*\s?/, '').trim())
    .filter((line) => line && !line.startsWith('@') && !/^Created by\b/i.test(line))
    .join('\n')
    .replace(/\{@link\s+([^} ]+)(?:\s+([^}]+))?\}/g, (_, target, label) => `\`${label || target}\``)
    .replace(/\{@code\s+([^}]+)\}/g, '`$1`')
    .replace(/<p>/gi, '\n\n')
    .replace(/<br\s*\/?>/gi, '\n')
    .replace(/<[^>]+>/g, '');

  return body
    .split(/\n{2,}/)
    .map((paragraph) => normalizeWhitespace(paragraph))
    .filter(Boolean)
    .join('\n\n');
}

function normalizeWhitespace(value) {
  return value.replace(/\s+/g, ' ').trim();
}

function ensurePackage(packages, packageName) {
  if (!packages.has(packageName)) {
    packages.set(packageName, { name: packageName, loaders: new Set(), classes: [] });
  }
  return packages.get(packageName);
}

function uniqueBy(items, keyFn) {
  const seen = new Set();
  const result = [];
  for (const item of items) {
    const key = keyFn(item);
    if (seen.has(key)) continue;
    seen.add(key);
    result.push(item);
  }
  return result;
}

function escapeRegex(value) {
  return value.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
}

async function writePackagePages(packages) {
  const packageDir = path.join(generatedRoot, 'packages');
  await mkdir(packageDir, { recursive: true });

  for (const packageEntry of [...packages.values()].sort((a, b) => a.name.localeCompare(b.name))) {
    const file = path.join(packageDir, `${safeFileName(packageEntry.name)}.mdx`);
    const classes = packageEntry.classes.sort((a, b) => a.qualifiedName.localeCompare(b.qualifiedName));
    await writeFile(file, `---
title: ${yamlString(packageEntry.name)}
description: Generated package reference for ${packageEntry.name}.
---

<div class="api-meta-grid">
  <div><strong>Loaders</strong>${[...packageEntry.loaders].sort().join(', ')}</div>
  <div><strong>Types</strong>${classes.length}</div>
</div>

## Types

${classes.map((type) => `- [${type.name}](../../classes/${type.qualifiedName.replaceAll('.', '/')}/) <span class="api-chip">${type.kind}</span>`).join('\n')}
`);
  }
}

async function writeClassPages(classes) {
  const classRoot = path.join(generatedRoot, 'classes');
  for (const type of classes) {
    const file = path.join(classRoot, ...type.qualifiedName.split('.')) + '.mdx';
    await mkdir(path.dirname(file), { recursive: true });
    await writeFile(file, `---
title: ${yamlString(type.name)}
description: ${yamlString(`Generated ${type.kind} reference for ${type.qualifiedName}.`)}
---

<div class="api-meta-grid">
  <div><strong>Package</strong><span class="api-chip">${escapeHtml(type.packageName)}</span></div>
  <div><strong>Loader</strong>${escapeHtml(type.loader)}</div>
  <div><strong>Kind</strong>${escapeHtml(type.kind)}</div>
  <div class="api-meta-source"><strong>Source</strong>${escapeHtml(type.sourcePath)}</div>
</div>

${mdxText(type.javadoc || 'No Javadoc summary was found for this type.')}

## Declaration

\`\`\`java
${type.declaration}
\`\`\`

${renderFields(type.fields)}

${renderMethods(type.methods)}
`);
  }
}

async function writeEventPages(events) {
  const eventRoot = path.join(generatedRoot, 'events');
  await mkdir(eventRoot, { recursive: true });

  const byGroup = Map.groupBy ? Map.groupBy(events, (event) => event.declaringType) : groupBy(events, (event) => event.declaringType);

  for (const [declaringType, groupEvents] of byGroup.entries()) {
    const groupName = declaringType.split('.').at(-1);
    const file = path.join(eventRoot, `${safeFileName(groupName)}.mdx`);
    await writeFile(file, `---
title: ${yamlString(groupName)}
description: Generated event reference for ${declaringType}.
---

<div class="api-meta-grid">
  <div><strong>Declaring Type</strong><span class="api-chip">${escapeHtml(declaringType)}</span></div>
  <div><strong>Events</strong>${groupEvents.length}</div>
</div>

${groupEvents.map(renderEvent).join('\n\n')}
`);
  }
}

function renderFields(fields) {
  if (!fields.length) return '';
  return `## Public Fields

${fields.map((field) => `### ${field.name}

<span class="api-chip">${escapeHtml(field.type)}</span>

${mdxText(field.javadoc || '')}
`).join('\n')}
`;
}

function renderMethods(methods) {
  if (!methods.length) return '';
  return `## Public Methods

${methods.map((method) => `### ${method.name}

\`\`\`java
${method.signature}
\`\`\`

${mdxText(method.javadoc || '')}
`).join('\n')}
`;
}

function renderEvent(event) {
  return `<h2 id="${slugId(event.name)}" class="api-event-title"><code>${escapeHtml(event.name)}</code></h2>

<div class="api-event-meta">
  <span><strong>Handler</strong><span class="api-chip">${escapeHtml(event.handler)}</span></span>
  <span><strong>Cancellable</strong>${event.cancellable ? 'Yes' : 'No'}</span>
</div>

${mdxText(event.javadoc || 'No Javadoc summary was found for this event.')}

${event.method ? `\`\`\`java\n${event.method.signature}\n\`\`\`` : ''}
`;
}

async function writeApiIndex({ classes, events, packages }) {
  const apiIndex = {
    project: {
      name: 'PolyLib',
      version: gradleProperties.version,
      minecraftVersion: gradleProperties.minecraft_version,
      releaseTag,
      generatedAt: new Date().toISOString(),
    },
    packages: [...packages.values()].sort((a, b) => a.name.localeCompare(b.name)).map((entry) => ({
      name: entry.name,
      loaders: [...entry.loaders].sort(),
      classCount: entry.classes.length,
    })),
    classes: classes.map((type) => ({
      name: type.name,
      qualifiedName: type.qualifiedName,
      packageName: type.packageName,
      loader: type.loader,
      kind: type.kind,
      sourcePath: type.sourcePath,
      summary: type.javadoc,
      methodCount: type.methods.length,
      fieldCount: type.fields.length,
      methods: type.methods,
      fields: type.fields,
    })),
    events,
  };
  await writeFile(path.join(publicRoot, 'api-index.json'), `${JSON.stringify(apiIndex, null, 2)}\n`);
}

async function writeLlmFiles({ classes, events, packages }) {
  const latestBase = 'https://creeperhost.github.io/PolyLib/latest';
  const generatedAt = new Date().toISOString();
  const packageSummaries = [...packages.values()]
    .sort((a, b) => a.name.localeCompare(b.name))
    .map((entry) => `- ${entry.name}: ${entry.classes.length} public types (${[...entry.loaders].sort().join(', ')})`)
    .join('\n');
  const eventSummaries = events
    .map((event) => `- ${event.qualifiedName}: handler ${event.handler}${event.method ? `, ${event.method.signature}` : ''}${event.cancellable ? ', cancellable' : ''}`)
    .join('\n');
  const typeSummaries = classes
    .map((type) => `- ${type.qualifiedName} (${type.kind}, ${type.loader}): ${type.javadoc || 'No summary.'}`)
    .join('\n');

  const llms = `# PolyLib

> PolyLib is a Minecraft library mod by CreeperHost. These docs include human guides, generated Java API reference, and release-specific metadata.

Generated for ${releaseTag} on ${generatedAt}.

Version: ${releaseTag}
Minecraft: ${gradleProperties.minecraft_version}
Java: ${gradleProperties.java_version}
Loaders: common, Fabric, NeoForge

## Start Here

- [Getting Started](${latestBase}/getting-started/)
- [Registries](${latestBase}/concepts/registries/)
- [Events](${latestBase}/concepts/events/)
- [Modular GUI](${latestBase}/concepts/modular-gui/)
- [Data Sync](${latestBase}/concepts/data-sync/)
- [Generated Reference](${latestBase}/reference/)

## Machine-readable

- [API index](${latestBase}/api-index.json)

## Packages

${packageSummaries}

## Events

${eventSummaries}

## Public Types

${typeSummaries}
`;

  await writeFile(path.join(publicRoot, 'llms.txt'), llms);
  await rm(path.join(publicRoot, 'llms-full.txt'), { force: true });
}

function groupBy(items, keyFn) {
  const map = new Map();
  for (const item of items) {
    const key = keyFn(item);
    if (!map.has(key)) map.set(key, []);
    map.get(key).push(item);
  }
  return map;
}

function safeFileName(value) {
  return value.replace(/[^A-Za-z0-9_.-]/g, '-');
}

function slugId(value) {
  return value.toLowerCase().replace(/[^a-z0-9]+/g, '-').replace(/^-|-$/g, '');
}

function yamlString(value) {
  return JSON.stringify(value);
}

function escapeHtml(value) {
  return String(value)
    .replaceAll('&', '&amp;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;');
}

function mdxText(value) {
  return String(value)
    .replaceAll('&', '&amp;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;')
    .replaceAll('{', '&#123;')
    .replaceAll('}', '&#125;');
}
