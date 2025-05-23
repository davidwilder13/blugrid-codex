function cleanJavadoc(javadoc: string | null): string | undefined {
    if (!javadoc) return undefined
    return javadoc
        .replace(/(^\s*\*\s*)/gm, '') // remove leading '*'
        .replace(/\r?\n|\r/g, ' ')    // collapse newlines to spaces
        .trim()
}

export { cleanJavadoc }
