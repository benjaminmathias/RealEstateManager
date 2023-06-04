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

    // Retrieve List<RealEstateEntity> as a Flow<List> then convert it to RealEstate
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

    // Retrieve RealEstateEntity as a Flow then convert it to RealEstate
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

    // Retrieve both user added RealEstate and PhotoItem, then insert it in db
    override suspend fun insertRealEstateAndPhoto(realEstate: RealEstate) {
        // Retrieve our RealEstate object and convert it to entity
        val realEstateEntity = RealEstateEntity(
            type = realEstate.type,
            surface = realEstate.surface,
            price = realEstate.price,
            description = realEstate.description,
            address = realEstate.address,
            isAvailable = realEstate.isAvailable,
            nearbyPOI = realEstate.nearbyPOI as ArrayList<String>,
            entryDate = realEstate.entryDate,
            saleDate = realEstate.saleDate,
            assignedAgent = realEstate.assignedAgent,
            room = realEstate.room,
            bedroom = realEstate.bedroom,
            bathroom = realEstate.bathroom,
        )

        // here we retrieve the newly inserted RealEstateEntity id
        val insertedId: Long = realEstateDao.insert(realEstateEntity)

        // Retrieve PhotoItem (if it exists) and convert it to entity
        val photoList = realEstate.photos?.map {
            RealEstatePhotoEntity(
                uri = it.uri,
                photoDescription = it.photoDescription,
                realEstateId = insertedId
            )
        }

        // If the user added a single or multiple photos, add it to db
        if (photoList != null) {
            val count = photoList.size
            for (i in 0 until count) {
                photoItemDao.insert(photoList[i])
            }
        }
    }

    // Retrieve all PhotoItem with id related to RealEstateEntity
    private suspend fun fetchAllPhotoItem(id: Long): List<PhotoItem> {
        val listOfEntity: List<RealEstatePhotoEntity> = photoItemDao.getById(id)

        val listOfPhotos: List<PhotoItem> = listOfEntity.map { entities ->
            PhotoItem(
                entities.uri,
                entities.photoDescription
            )
        }
        return listOfPhotos
    }

    // Update an existing RealEstateEntity when user set it as no longer available
    override suspend fun setRealEstateAsNoLongerAvailable(saleDate: String, isAvailable: Boolean, id: Long) {
        Log.d("RealEstateRepository", "Update called")
        realEstateDao.updateRealEstate(saleDate, isAvailable, id)
    }

    override suspend fun editRealEstate(realEstate: RealEstate) {
        // Retrieve our RealEstate object and convert it to entity
        val realEstateEntity = convertRealEstateToEntity(realEstate)

        // here we update the realEstateEntity
        realEstateDao.update(realEstateEntity)

        // Retrieve PhotoItem from the updated realEstate and convert it to entity
        val photoList = realEstate.photos?.map {
            realEstateEntity.id?.let { it1 ->
                RealEstatePhotoEntity(
                    uri = it.uri,
                    photoDescription = it.photoDescription,
                    realEstateId = it1
                )
            }
        }

        if (photoList != null) {
            photoList.forEach { photo ->
                if (photo != null) {
                    photoItemDao.deletePhotosByRealEstateId(photo.realEstateId)
                }
            }

            val count = photoList.size
            for (i in 0 until count) {
                photoList[i]?.let { photoItemDao.insert(it) }
            }
        }
    }

    private fun convertRealEstateToEntity(realEstate: RealEstate): RealEstateEntity {
        return RealEstateEntity(
            type = realEstate.type,
            surface = realEstate.surface,
            price = realEstate.price,
            description = realEstate.description,
            address = realEstate.address,
            isAvailable = realEstate.isAvailable,
            nearbyPOI = realEstate.nearbyPOI as ArrayList<String>,
            entryDate = realEstate.entryDate,
            saleDate = realEstate.saleDate,
            assignedAgent = realEstate.assignedAgent,
            room = realEstate.room,
            bedroom = realEstate.bedroom,
            bathroom = realEstate.bathroom,
            id = realEstate.id
        )
    }
}