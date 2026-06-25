import { defineConfig } from 'astro/config';
import starlight from '@astrojs/starlight';

const docsBase = process.env.DOCS_BASE || '/PolyLib';

export default defineConfig({
  site: 'https://creeperhost.github.io',
  base: docsBase,
  integrations: [
    starlight({
      title: 'PolyLib',
      description: 'Modern documentation and generated API reference for PolyLib.',
      logo: {
        src: './src/assets/creeperhost-logo.svg',
        alt: 'CreeperHost',
        replacesTitle: true,
      },
      favicon: '/favicon.ico',
      customCss: ['./src/styles/creeperhost.css'],
      social: [
        {
          icon: 'github',
          label: 'GitHub',
          href: 'https://github.com/CreeperHost/PolyLib',
        },
      ],
      components: {
        ThemeSelect: './src/components/VersionSelect.astro',
      },
      tableOfContents: {
        minHeadingLevel: 2,
        maxHeadingLevel: 3,
      },
      sidebar: [
        {
          label: 'Developer Docs',
          items: [
            'getting-started',
            'release-versioning',
            'llm-ingestion',
          ],
        },
        {
          label: 'Concepts',
          items: [
            'concepts/registries',
            'concepts/events',
            'concepts/modular-gui',
            'concepts/data-sync',
            'concepts/networking',
            'concepts/accessibility',
          ],
        },
        {
          label: 'Generated Reference',
          items: [
            'reference',
            {
              label: 'Events',
              items: [{ autogenerate: { directory: 'reference/generated/events', collapsed: true } }],
            },
            {
              label: 'Packages',
              items: [{ autogenerate: { directory: 'reference/generated/packages', collapsed: true } }],
            },
            {
              label: 'Classes',
              items: [{ autogenerate: { directory: 'reference/generated/classes', collapsed: true } }],
            },
          ],
        },
      ],
      editLink: {
        baseUrl: 'https://github.com/CreeperHost/PolyLib/edit/multi/26.1.2/docs/',
      },
      credits: false,
    }),
  ],
});
