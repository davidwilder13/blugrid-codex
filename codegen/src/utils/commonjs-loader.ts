import { createRequire } from 'node:module'

const require = createRequire(import.meta.url)

export function requireCjs<T = any>(moduleId: string): T {
    return require(moduleId)
}
