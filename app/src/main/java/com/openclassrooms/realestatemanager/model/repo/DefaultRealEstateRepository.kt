package com.openclassrooms.realestatemanager.model.repo

import android.util.Log
import androidx.sqlite.db.SimpleSQLiteQuery
import com.openclassrooms.realestatemanager.model.data.Filters
import com.openclassrooms.realestatemanager.model.data.RealEstate
import com.openclassrooms.realestatemanager.model.data.RealEstateDao
import com.openclassrooms.realestatemanager.model.data.RealEstateEntity
import com.openclassrooms.realestatemanager.model.data.RealEstatePhoto
import com.openclassrooms.realestatemanager.model.data.RealEstatePhotoDao
import com.openclassrooms.realestatemanager.model.data.RealEstatePhotoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultRealEstateRepository @Inject constructor(
    private val realEstateDao: RealEstateDao, private val realEstatePhotoDao: RealEstatePhotoDao
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
                    lat = it.lat,
                    lon = it.lon,
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
                lat = entity.lat,
                lon = entity.lon,
                id = entity.id
            )
        }
    }

    // Retrieve a filtered list of RealEstateEntity as a Flow<List> then convert it to RealEstate
    override suspend fun retrieveAndConvertFilteredRealEstateList(
        type: String?,
        priceMin: String?,
        priceMax: String?,
        surfaceMin: String?,
        surfaceMax: String?,
        room: String?,
        bedroom: String?,
        bathroom: String?,
        location: String?,
        nearbyPOI: List<String>?
    ): Flow<List<RealEstate>> {
        Log.d("Repo", "Filter reached !")
        return getAllFiltered(
            Filters(
                type = type,
                priceMin = priceMin,
                priceMax = priceMax,
                surfaceMin = surfaceMin,
                surfaceMax = surfaceMax,
                room = room,
                bedroom = bedroom,
                bathroom = bathroom,
                location = location,
                nearbyPOI = nearbyPOI
            )
        ).map { entity: List<RealEstateEntity> ->
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
                        lat = it.lat,
                        lon = it.lon,
                        id = it.id
                    )
                }
            }


    }
    /* override suspend fun retrieveAndConvertFilteredRealEstateList(type: String?): List<RealEstate> {
         return realEstateDao.getAllFiltered(
             Filters(
                 type = type
             )
         ).map {
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
                 lat = it.lat,
                 lon = it.lon,
                 id = it.id
             )
         }
     }*/


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
            lat = realEstate.lat,
            lon = realEstate.lon
        )

        // here we retrieve the newly inserted RealEstateEntity id
        val insertedId: Long = realEstateDao.insert(realEstateEntity)

        // Retrieve PhotoItem (if it exists) and convert it to entity
        val photoList = realEstate.photos?.map {
            RealEstatePhotoEntity(
                uri = it.uri, photoDescription = it.photoDescription, realEstateId = insertedId
            )
        }

        // If the user added a single or multiple photos, add it to db
        if (photoList != null) {
            val count = photoList.size
            for (i in 0 until count) {
                realEstatePhotoDao.insert(photoList[i])
            }
        }
    }

    // Retrieve all PhotoItem with id related to RealEstateEntity
    private suspend fun fetchAllPhotoItem(id: Long): List<RealEstatePhoto> {
        val listOfEntity: List<RealEstatePhotoEntity> = realEstatePhotoDao.getById(id)

        val listOfPhotos: List<RealEstatePhoto> = listOfEntity.map { entities ->
            RealEstatePhoto(
                entities.uri, entities.photoDescription
            )
        }
        return listOfPhotos
    }

    // Update an existing RealEstateEntity when user set it as no longer available
    override suspend fun setRealEstateAsNoLongerAvailable(
        saleDate: String, isAvailable: Boolean, id: Long
    ) {
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
                    uri = it.uri, photoDescription = it.photoDescription, realEstateId = it1
                )
            }
        }

        if (photoList != null) {
            photoList.forEach { photo ->
                if (photo != null) {
                    realEstatePhotoDao.deletePhotosByRealEstateId(photo.realEstateId)
                }
            }

            val count = photoList.size
            for (i in 0 until count) {
                photoList[i]?.let { realEstatePhotoDao.insert(it) }
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
            lat = realEstate.lat,
            lon = realEstate.lon,
            id = realEstate.id
        )
    }

    private fun getAllFiltered(filters: Filters): Flow<List<RealEstateEntity>> {
        val conditions = mutableListOf<String>()
        with(filters) {
            type?.let { conditions.add("type LIKE '$it'") }
            location?.let { conditions.add("address LIKE '%$it%'") }
            priceMin?.let { conditions.add("price >= $it") }
            priceMax?.let { conditions.add("price <= $it") }
            surfaceMin?.let { conditions.add("surface >= $it") }
            surfaceMax?.let { conditions.add("surface <= $it") }
            room?.let { conditions.add("room >= $it") }
            bedroom?.let { conditions.add("bedroom >= $it") }
            bathroom?.let { conditions.add("bathroom >= $it") }
            nearbyPOI?.let { conditions.add("nearbyPOI IN ($it)") }
            // Handle all filters here
        }

       /* if (conditions.isEmpty())
            return getAll()*/

        val conditionsMerged = conditions.joinToString(separator = " AND ")
        // val bindArgs = conditions.map

        val query = SimpleSQLiteQuery(
            "SELECT * FROM realEstate WHERE $conditionsMerged",
            // bindArgs.toTypedArray()
        )

        Log.d("Repo", "Query : SELECT * FROM realEstate WHERE $conditionsMerged")

        return realEstateDao.getAllFiltered(query)
    }
}