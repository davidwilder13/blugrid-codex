import path from 'path'
import { fileURLToPath } from 'url'

const scriptDir = path.dirname(fileURLToPath(import.meta.url))

// Assumes this utility file is under src/utils/... → goes up to project root
const projectRoot = path.resolve(scriptDir, '../../')

export function resolveFromProjectRoot(...segments: string[]): string {
    return path.resolve(projectRoot, ...segments)
}
