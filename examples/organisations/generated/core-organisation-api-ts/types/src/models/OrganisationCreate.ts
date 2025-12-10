/**
 * Input for creating a new Organisation.
 * Auto-generated from JDL entity definition.
 */
export class OrganisationCreate {
  constructor(
    public readonly parentOrganisationId: number,
    public readonly effectiveTimestamp: Date,
  ) {}

  /**
   * Create a OrganisationCreate instance from a JSON object.
   */
  static fromJson(json: unknown): OrganisationCreate {
    const obj = json as Record<string, unknown>;
    return new OrganisationCreate(
      Number(obj.parentOrganisationId),
      new Date(obj.effectiveTimestamp as string),
    );
  }

  /**
   * Convert this instance to a JSON-serializable object for API requests.
   */
  toJson(): Record<string, unknown> {
    return {
      parentOrganisationId: this.parentOrganisationId,
      effectiveTimestamp: this.effectiveTimestamp.toISOString(),
    };
  }
}