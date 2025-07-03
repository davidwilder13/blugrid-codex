import path from 'path';
import { CodegenConfig } from '../config/codegen-config.js';

/**
 * Resolves a full path to a template file in the Kotlin template directory.
 * Optionally specify a subfolder.
 */
export function resolveTemplate(templateName: string, subdir: string = 'common'): string {
    return path.join(CodegenConfig.kotlin.templateBase, subdir, templateName);
}
