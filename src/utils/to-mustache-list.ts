import { map } from 'lodash-es'

/**
 * Adds Mustache-compatible metadata to an array:
 * - index
 * - first (true for first item)
 * - last (true for last item)
 */
export function toMustacheList<T extends object>(items: T[]): MustacheListItem<T>[]
export function toMustacheList<T>(items: T[]): MustacheListItem<T>[]
export function toMustacheList<T>(items: T[]): any[] {
    return map(items, (item, index) =>
        typeof item === 'object' && item !== null
            ? { ...item, index, first: index === 0, last: index === items.length - 1 }
            : { value: item, index, first: index === 0, last: index === items.length - 1 }
    )
}

export interface MustacheListMeta {
    index: number
    first: boolean
    last: boolean
}

export interface MustacheStringList<T> extends MustacheListMeta {
    value: T
}


export type MustacheListItem<T> = (T & MustacheListMeta)
