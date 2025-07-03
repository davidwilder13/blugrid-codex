**How to use it:**

1. Globally link your generator (from the repo root):    
```
cd <path-to-jdl-codegen-tool>
pnpm install
pnpm run build
pnpm link --global
```

2. Run the example:
```
cd examples/organisationsApi
pnpm link @blugrid/api-codegen
pnpm run generate   # or npm run generate
```

This will invoke the `api-codegen generate` command against the JDL definition in `examples/organisationsApi/jdl/core-organisation.jdl`.
