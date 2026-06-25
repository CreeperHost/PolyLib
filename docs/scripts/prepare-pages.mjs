import { cp, mkdir, readFile, readdir, rm, writeFile } from 'node:fs/promises';
import path from 'node:path';

const args = parseArgs(process.argv.slice(2));
const tag = args.tag || process.env.GITHUB_REF_NAME;
const dist = path.resolve(args.dist || 'docs/dist');
const out = path.resolve(args.out || '.pages');
const updateLatest = args.updateLatest === 'true'
  || args['update-latest'] === 'true'
  || process.env.UPDATE_LATEST === 'true';

if (!tag) {
  throw new Error('A release tag is required. Pass --tag <tag> or run from a tag-triggered GitHub Action.');
}

const latestDir = path.join(out, 'latest');
const versionDir = path.join(out, 'versions', tag);

await mkdir(out, { recursive: true });
await mkdir(path.join(out, 'versions'), { recursive: true });
await rm(versionDir, { recursive: true, force: true });
await cp(dist, versionDir, { recursive: true });
await rewriteTextFiles(versionDir, '/PolyLib/latest/', `/PolyLib/versions/${tag}/`);
await rewriteTextFiles(versionDir, '/PolyLib/latest', `/PolyLib/versions/${tag}`);

if (updateLatest) {
  await rm(latestDir, { recursive: true, force: true });
  await cp(dist, latestDir, { recursive: true });
}

const versionsPath = path.join(out, 'versions.json');
const existing = await readJson(versionsPath, { latest: null, versions: [] });
const latest = updateLatest ? tag : existing.latest;
const versions = [
  { version: tag, url: `/PolyLib/versions/${tag}/`, latest: latest === tag, publishedAt: new Date().toISOString() },
  ...existing.versions.filter((entry) => entry.version !== tag),
]
  .map((entry) => ({ ...entry, latest: latest ? entry.version === latest : false }))
  .sort((a, b) => compareVersions(b.version, a.version));

await writeFile(versionsPath, `${JSON.stringify({ latest, versions }, null, 2)}\n`);

if (updateLatest) {
  await writeFile(path.join(out, 'index.html'), redirectHtml('/PolyLib/latest/'));

  await rm(path.join(out, 'font'), { recursive: true, force: true });

  await rm(path.join(out, 'llms-full.txt'), { force: true });
  for (const file of ['llms.txt', 'api-index.json']) {
    await cp(path.join(latestDir, file), path.join(out, file));
  }
}

console.log(`Prepared GitHub Pages tree for ${tag} at ${out}${updateLatest ? ' and updated latest' : ''}`);

function parseArgs(argv) {
  const result = {};
  for (let index = 0; index < argv.length; index += 1) {
    if (!argv[index].startsWith('--')) continue;
    result[argv[index].slice(2)] = argv[index + 1];
    index += 1;
  }
  return result;
}

async function readJson(file, fallback) {
  try {
    return JSON.parse(await readFile(file, 'utf8'));
  } catch {
    return fallback;
  }
}

async function rewriteTextFiles(dir, from, to) {
  const entries = await readdir(dir, { withFileTypes: true });
  await Promise.all(entries.map(async (entry) => {
    const file = path.join(dir, entry.name);
    if (entry.isDirectory()) {
      await rewriteTextFiles(file, from, to);
      return;
    }

    if (!/\.(css|html|js|json|mjs|txt|xml)$/.test(entry.name)) return;
    const text = await readFile(file, 'utf8');
    if (!text.includes(from)) return;
    await writeFile(file, text.replaceAll(from, to));
  }));
}

function compareVersions(left, right) {
  return left.localeCompare(right, undefined, { numeric: true, sensitivity: 'base' });
}

function redirectHtml(target) {
  return `<!doctype html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="refresh" content="0; url=${target}">
    <link rel="canonical" href="${target}">
    <title>PolyLib documentation</title>
  </head>
  <body>
    <p><a href="${target}">PolyLib documentation</a></p>
  </body>
</html>
`;
}
