/**
 * Input for updating an existing Organisation.
 * All fields are optional for partial updates.
 * Auto-generated from JDL entity definition.
 */
export class OrganisationUpdate {
  constructor(
    public readonly parentOrganisationId?: number,
    public readonly effectiveTimestamp?: Date,
  ) {}

  /**
   * Create a OrganisationUpdate instance from a JSON object.
   */
  static fromJson(json: unknown): OrganisationUpdate {
    const obj = json as Record<string, unknown>;
    return new OrganisationUpdate(
      obj.parentOrganisationId !== undefined && obj.parentOrganisationId !== null ? Number(obj.parentOrganisationId) : undefined,
      obj.effectiveTimestamp ? new Date(obj.effectiveTimestamp as string) : undefined,
    );
  }

  /**
   * Convert this instance to a JSON-serializable object for API requests.
   * Only includes defined fields.
   */
  toJson(): Record<string, unknown> {
    const result: Record<string, unknown> = {};
    if (this.parentOrganisationId !== undefined) {
      result.parentOrganisationId = this.parentOrganisationId;
    }
    if (this.effectiveTimestamp !== undefined) {
      result.effectiveTimestamp = this.effectiveTimestamp?.toISOString();
    }
    return result;
  }
}