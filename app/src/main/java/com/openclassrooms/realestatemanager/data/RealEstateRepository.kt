package com.openclassrooms.realestatemanager.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealEstateRepository @Inject constructor(
    private val realEstateDao: RealEstateDao,
    private val photoItemDao: PhotoItemDao
) {

    // Retrieve List<RealEstateEntity> as a Flow<List< then convert it to RealEstate
    fun retrieveAndConvertRealEstateEntityList(): Flow<List<RealEstate>> {
        return realEstateDao.getAllRealEstateFlow().map { entity: List<RealEstateEntity> ->
            entity.map {
                RealEstate(
                    it.type,
                    it.surface,
                    it.price,
                    it.description,
                    it.address,
                    it.isAvailable,
                    it.id?.let { it1 -> fetchAllPhotoItem(it1) },
                    it.entryDate,
                    it.saleDate,
                    it.assignedAgent,
                    it.room,
                    it.bedroom,
                    it.bathroom,
                    it.id
                )
            }
        }
    }

    // TODO : check avec Valdèse
    // Retrieve RealEstateEntity as a Flow< then convert it to RealEstate
    fun retrieveAndConvertSpecificRealEstateEntityTest(id: Long): Flow<RealEstate> {

        return realEstateDao.getSpecificRealEstate(id).map { entity: RealEstateEntity ->
            RealEstate(
                entity.type,
                entity.surface,
                entity.price,
                entity.description,
                entity.address,
                entity.isAvailable,
                fetchAllPhotoItem(id),
                entity.entryDate,
                entity.saleDate,
                entity.assignedAgent,
                entity.room,
                entity.bedroom,
                entity.bathroom,
                entity.id
            )
        }
    }


  /*  fun retrieveAndConvertSpecificRealEstateEntity(id: Long): LiveData<RealEstate> {

        return realEstateDao.getSpecificRealEstate(id).map { entity: RealEstateEntity ->
            RealEstate(
                entity.type,
                entity.surface,
                entity.price,
                entity.description,
                entity.address,
                entity.isAvailable,
                null,
                entity.entryDate,
                entity.saleDate,
                entity.assignedAgent,
                entity.room,
                entity.bedroom,
                entity.bathroom,
                entity.id
            )
        }
    }*/

    suspend fun insertRealEstateDTO(realEstate: RealEstate) {
        // Retrieve our RealEstate object and convert it to entity
        val realEstateEntity = RealEstateEntity(
            realEstate.type,
            realEstate.surface,
            realEstate.price,
            realEstate.description,
            realEstate.address,
            realEstate.isAvailable,
            realEstate.entryDate,
            realEstate.saleDate,
            realEstate.assignedAgent,
            realEstate.room,
            realEstate.bedroom,
            realEstate.bathroom,
        )

        // Retrieve PhotoItem and convert it to entity
        val photoList = realEstate.photos?.get(0)?.let {
            // here we retrieve the newly inserted RealEstateEntity id
            val insertedId: Long = realEstateDao.insert(realEstateEntity)
            PhotoEntity(
                it.uri,
                it.photoDescription,
                insertedId
            )
        }

        if (photoList != null) {
            photoItemDao.insert(photoList)
        }
    }

    // PhotoItem
    private suspend fun fetchAllPhotoItem(id: Long): List<PhotoItem> {
        val listOfEntity: List<PhotoEntity> = photoItemDao.getAllSpecificPhotos(id)

        val listOfPhotos: List<PhotoItem> = listOfEntity.map { entities ->
            PhotoItem(
                entities.uri,
                entities.photoDescription
            )
        }
        return listOfPhotos
    }
}