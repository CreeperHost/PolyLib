# PolyLib Docs

This Starlight site is generated and published automatically for release tags.

Local commands:

```bash
cd docs
pnpm install
pnpm run generate
pnpm run dev
pnpm run build
```

The generator reads the Java sources from `common`, `fabric`, and `neoforge`, then writes release-specific reference pages plus `api-index.json`, `llms.txt`, and `llms-full.txt`.
