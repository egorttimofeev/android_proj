package com.example.myapplication.data.mapper

import com.example.myapplication.data.local.entity.ImageEntity
import com.example.myapplication.data.local.entity.RoomEntity
import com.example.myapplication.data.local.entity.UserEntity
import com.example.myapplication.data.model.ImageItem
import com.example.myapplication.data.model.User

// User mappers
fun User.toEntity(token: String): UserEntity {
    return UserEntity(
        id = this.id,
        username = this.username,
        email = this.email,
        token = token
    )
}

fun UserEntity.toModel(): User {
    return User(
        id = this.id,
        username = this.username,
        email = this.email,
        token = this.token
    )
}

// Image mappers
fun ImageItem.toEntity(): ImageEntity {
    return ImageEntity(
        id = this.id,
        url = this.url,
        title = this.title,
        description = this.description,
        createdAt = this.createdAt,
        thumbnailUrl = this.thumbnailUrl
    )
}

fun ImageEntity.toModel(): ImageItem {
    return ImageItem(
        id = this.id,
        url = this.url,
        title = this.title,
        description = this.description,
        createdAt = this.createdAt,
        thumbnailUrl = this.thumbnailUrl
    )
}

fun List<ImageItem>.toEntityList(): List<ImageEntity> {
    return this.map { it.toEntity() }
}

fun List<ImageEntity>.toModelList(): List<ImageItem> {
    return this.map { it.toModel() }
}

// Room mappers
fun com.example.myapplication.data.model.Room.toEntity(): RoomEntity {
    return RoomEntity(
        id = this.id,
        name = this.name,
        description = this.description,
        imageUrl = this.imageUrl,
        capacity = this.capacity,
        price = this.price,
        amenities = this.amenities,
        status = this.status.name,
        createdAt = this.createdAt
    )
}

fun RoomEntity.toModel(): com.example.myapplication.data.model.Room {
    return com.example.myapplication.data.model.Room(
        id = this.id,
        name = this.name,
        description = this.description,
        imageUrl = this.imageUrl,
        capacity = this.capacity,
        price = this.price,
        amenities = this.amenities,
        status = com.example.myapplication.data.model.RoomStatus.valueOf(this.status),
        createdAt = this.createdAt
    )
}

fun List<com.example.myapplication.data.model.Room>.toRoomEntityList(): List<RoomEntity> {
    return this.map { it.toEntity() }
}

fun List<RoomEntity>.toRoomModelList(): List<com.example.myapplication.data.model.Room> {
    return this.map { it.toModel() }
}
