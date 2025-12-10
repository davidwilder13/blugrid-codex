/**
 * Organisation model class.
 * Auto-generated from JDL entity definition.
 */
export class Organisation {
  constructor(
    public readonly id: number,
    public readonly uuid: string,
    public readonly parentOrganisationId: number,
    public readonly effectiveTimestamp: Date,
    public readonly createdDate?: Date,
    public readonly createdBy?: string,
    public readonly updatedDate?: Date,
    public readonly updatedBy?: string,
  ) {}

  /**
   * Create a Organisation instance from a JSON object.
   * Handles type coercion for dates, numbers, and booleans.
   */
  static fromJson(json: unknown): Organisation {
    const obj = json as Record<string, unknown>;
    return new Organisation(
      Number(obj.id),
      String(obj.uuid),
      Number(obj.parentOrganisationId),
      new Date(obj.effectiveTimestamp as string),
      obj.createdDate ? new Date(obj.createdDate as string) : undefined,
      obj.createdBy ? String(obj.createdBy) : undefined,
      obj.updatedDate ? new Date(obj.updatedDate as string) : undefined,
      obj.updatedBy ? String(obj.updatedBy) : undefined,
    );
  }

  /**
   * Convert this instance to a JSON-serializable object.
   */
  toJson(): Record<string, unknown> {
    return {
      id: this.id,
      uuid: this.uuid,
      parentOrganisationId: this.parentOrganisationId,
      effectiveTimestamp: this.effectiveTimestamp.toISOString(),
      createdDate: this.createdDate?.toISOString(),
      createdBy: this.createdBy,
      updatedDate: this.updatedDate?.toISOString(),
      updatedBy: this.updatedBy,
    };
  }
}