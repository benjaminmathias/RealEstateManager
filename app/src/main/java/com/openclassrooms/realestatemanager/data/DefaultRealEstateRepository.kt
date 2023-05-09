package com.openclassrooms.realestatemanager.data

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultRealEstateRepository @Inject constructor(
    private val realEstateDao: RealEstateDao,
    private val photoItemDao: PhotoItemDao
) : RealEstateRepository {

    // Retrieve List<RealEstateEntity> as a Flow<List< then convert it to RealEstate
    override fun retrieveAndConvertRealEstateEntityList(): Flow<List<RealEstate>> {
        return realEstateDao.getAll().map { entity: List<RealEstateEntity> ->
            entity.map {
                RealEstate(
                    type = it.type,
                    surface = it.surface,
                    price = it.price,
                    description = it.description,
                    address = it.address,
                    nearbyPOI = it.nearbyPOI,
                    isAvailable = it.isAvailable,
                    photos = it.id?.let { it1 -> fetchAllPhotoItem(it1) },
                    entryDate = it.entryDate,
                    saleDate = it.saleDate,
                    assignedAgent = it.assignedAgent,
                    room = it.room,
                    bedroom = it.bedroom,
                    bathroom = it.bathroom,
                    id = it.id
                )
            }
        }
    }

    // Retrieve RealEstateEntity as a Flow< then convert it to RealEstate
    override fun retrieveAndConvertSpecificRealEstateEntity(id: Long): Flow<RealEstate> {

        return realEstateDao.getById(id).map { entity: RealEstateEntity ->
            RealEstate(
                type = entity.type,
                surface = entity.surface,
                price = entity.price,
                description = entity.description,
                address = entity.address,
                isAvailable = entity.isAvailable,
                photos = fetchAllPhotoItem(id),
                nearbyPOI = entity.nearbyPOI,
                entryDate = entity.entryDate,
                saleDate = entity.saleDate,
                assignedAgent = entity.assignedAgent,
                room = entity.room,
                bedroom = entity.bedroom,
                bathroom = entity.bathroom,
                id = entity.id
            )
        }
    }

    override suspend fun insertRealEstateAndPhoto(realEstate: RealEstate) {
        // Retrieve our RealEstate object and convert it to entity
        val realEstateEntity = RealEstateEntity(
            type = realEstate.type,
            surface = realEstate.surface,
            price = realEstate.price,
            description = realEstate.description,
            address = realEstate.address,
            isAvailable = realEstate.isAvailable,
            nearbyPOI = realEstate.nearbyPOI,
            entryDate = realEstate.entryDate,
            saleDate = realEstate.saleDate,
            assignedAgent = realEstate.assignedAgent,
            room = realEstate.room,
            bedroom = realEstate.bedroom,
            bathroom = realEstate.bathroom,
        )

        // here we retrieve the newly inserted RealEstateEntity id
        val insertedId: Long = realEstateDao.insert(realEstateEntity)

        // Retrieve PhotoItem and convert it to entity
        val photoList = realEstate.photos?.map {
            PhotoEntity(
                uri = it.uri,
                photoDescription = it.photoDescription,
                realEstateId = insertedId
            )
        }

        if (photoList != null) {
            val count = photoList.size
            for (i in 0 until count) {
                photoItemDao.insert(photoList[i])
            }
        }
    }

    // PhotoItem
    private suspend fun fetchAllPhotoItem(id: Long): List<PhotoItem> {
        val listOfEntity: List<PhotoEntity> = photoItemDao.getById(id)

        val listOfPhotos: List<PhotoItem> = listOfEntity.map { entities ->
            PhotoItem(
                entities.uri,
                entities.photoDescription
            )
        }
        return listOfPhotos
    }

    override suspend fun updateRealEstate(saleDate: String, isAvailable: Boolean, id: Long) {
        Log.d("RealEstateRepository", "Update called")
        realEstateDao.updateRealEstate(saleDate, isAvailable, id)
    }
}