const gradlePropertiesTemplate = String.raw`
micronautVersion=4.4.3
kotlinVersion=1.9.23
`;

export const GradlePropertiesTemplate = (): string =>
    gradlePropertiesTemplate;
