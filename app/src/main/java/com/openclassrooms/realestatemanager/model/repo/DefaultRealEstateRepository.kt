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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultRealEstateRepository @Inject constructor(
    private val realEstateDao: RealEstateDao,
    private val realEstatePhotoDao: RealEstatePhotoDao,
    private val ioDispatcher: CoroutineDispatcher,
) : RealEstateRepository {

    private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    // READ
    // Retrieve List<RealEstateEntity> as a Flow<List> then convert it to RealEstate
    override fun retrieveAndConvertRealEstateEntityList(): Flow<List<RealEstate>> {
        return realEstateDao.getAll().map { entity: List<RealEstateEntity> ->
            entity.map {
                convertEntityToRealEstate(it)
            }
        }
    }

    // Retrieve a single RealEstateEntity as a Flow then convert it to RealEstate
    override fun retrieveAndConvertSpecificRealEstateEntity(id: Long): Flow<RealEstate> {
        return realEstateDao.getById(id).map {
            convertEntityToRealEstate(it)
        }
    }

    // Retrieve a filtered list of RealEstateEntity as a Flow<List> then convert it to RealEstate
    override suspend fun retrieveAndConvertFilteredRealEstateList(
        filters: Filters
    ): List<RealEstate> {
        Log.d("Repo", "Filter reached !")
        return withContext(ioDispatcher) {
            realEstateDao.getAllFiltered(buildQuery(filters)).map { convertEntityToRealEstate(it) }
        }
    }

    // Build a specific SQL query to retrieve matching (multiple if exists) RealEstateEntity
    private fun buildQuery(filters: Filters): SimpleSQLiteQuery {

        val conditions = mutableListOf<String>()
        with(filters) {
            type?.let { conditions.add("type LIKE '$it'") }
            location?.let { conditions.add("address LIKE '%$it%'") }

            if (priceMin?.isNotEmpty() == true) {
                priceMin?.let { conditions.add("price >= $it") }
            }

            if (priceMax?.isNotEmpty() == true) {
                priceMax?.let { conditions.add("price <= $it") }
            }

            surfaceMin?.let { conditions.add("surface >= $it") }
            surfaceMax?.let { conditions.add("surface <= $it") }
            if (nearbyPOI != null) {
                for (element: String in nearbyPOI!!) {
                    nearbyPOI.let { conditions.add("nearbyPOI LIKE '%$element%'") }
                }
            }
            isAvailable?.let { conditions.add("isAvailable = $it") }
            fromListedDate?.let { conditions.add("date(entryDate) >= date('$it')") }
            toListedDate?.let { conditions.add("date(entryDate) <= date('$it')") }
            fromSoldDate?.let { conditions.add("date(saleDate) >= date('$it')") }
            toSoldDate?.let { conditions.add("date(saleDate) >= date('$it')") }
        }

        val conditionsMerged = conditions.joinToString(separator = " AND ")

        val query = SimpleSQLiteQuery(
            "SELECT * FROM realEstate WHERE $conditionsMerged"
        )
        Log.d("Repo", "Query : SELECT * FROM realEstate WHERE $conditionsMerged")

        return query
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


    // CREATE
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

    // UPDATE
    // Update an existing RealEstateEntity to no longer available (sold)
    /*override suspend fun setRealEstateAsNoLongerAvailable(
        saleDate: String, isAvailable: Boolean, id: Long
    ) {
        Log.d("RealEstateRepository", "Update called")
        realEstateDao.updateRealEstate(saleDate, isAvailable, id)
    }*/

    override suspend fun setRealEstateAsNoLongerAvailable(
        saleDate: OffsetDateTime, isAvailable: Boolean, id: Long
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

    fun convertRealEstateToEntity(realEstate: RealEstate): RealEstateEntity {
        return RealEstateEntity(
            type = realEstate.type,
            surface = realEstate.surface,
            price = realEstate.price,
            description = realEstate.description,
            address = realEstate.address,
            isAvailable = realEstate.isAvailable,
            nearbyPOI = realEstate.nearbyPOI as ArrayList<String>,
            // entryDate = realEstate.entryDate,
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

    suspend fun convertEntityToRealEstate(realEstateEntity: RealEstateEntity): RealEstate {
        return RealEstate(
            type = realEstateEntity.type,
            surface = realEstateEntity.surface,
            price = realEstateEntity.price,
            description = realEstateEntity.description,
            address = realEstateEntity.address,
            nearbyPOI = realEstateEntity.nearbyPOI,
            isAvailable = realEstateEntity.isAvailable,
            photos = realEstateEntity.id?.let { it1 -> fetchAllPhotoItem(it1) },
            // entryDate = realEstateEntity.entryDate,
            entryDate = realEstateEntity.entryDate,
            saleDate = realEstateEntity.saleDate,
            assignedAgent = realEstateEntity.assignedAgent,
            room = realEstateEntity.room,
            bedroom = realEstateEntity.bedroom,
            bathroom = realEstateEntity.bathroom,
            lat = realEstateEntity.lat,
            lon = realEstateEntity.lon,
            id = realEstateEntity.id
        )
    }
}