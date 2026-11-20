package com.pennypilot.app.data.mapper

import com.pennypilot.app.data.local.entity.CategoryEntity
import com.pennypilot.app.domain.model.Category
import com.pennypilot.app.domain.model.TransactionType

/**
 * Converts between Room's category representation and the domain model.
 */
fun CategoryEntity.toDomain(): Category =
    Category(
        id = id,
        name = name,
        type = TransactionType.valueOf(type),
        icon = icon,
        isDefault = isDefault
    )

fun Category.toEntity(): CategoryEntity =
    CategoryEntity(
        id = id,
        name = name,
        type = type.name,
        icon = icon,
        isDefault = isDefault
    )
