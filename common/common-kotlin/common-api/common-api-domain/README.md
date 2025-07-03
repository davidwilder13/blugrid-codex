# common-api-domain

This module defines **Blugrid's canonical domain primitives** for use across all Kotlin-based modules and code generation targets.

It provides strongly typed value classes to enforce correctness and consistency for commonly used concepts like IDs and timestamps. These types are used in generated APIs, database entities, and GraphQL/gRPC contracts.

## 📦 Provided Domain Types

| Type                  | Description                                                   |
|-----------------------|---------------------------------------------------------------|
| `IdentityID`          | A strongly typed wrapper for `Long` IDs                       |
| `IdentityUUID`        | A UUID-based identity type for external references            |
| `CreatedTimestamp`    | The time a resource was created (read-only field)             |
| `LastChangedTimestamp`| The last time a resource was modified (read-only field)       |
| `EffectiveTimestamp`  | The datetime when a resource becomes active or takes effect   |

These types are implemented as Kotlin [inline value classes](https://kotlinlang.org/docs/value-classes.html) for zero-cost abstraction and strong typing.

## 🔍 Usage

```kotlin
data class Organisation(
    val id: IdentityID,
    val uuid: IdentityUUID,
    val created: CreatedTimestamp,
    val effectiveTimestamp: EffectiveTimestamp
)
