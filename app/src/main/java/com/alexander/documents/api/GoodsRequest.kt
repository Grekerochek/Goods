package com.alexander.documents.api

import com.alexander.documents.entity.City
import com.alexander.documents.entity.Group
import com.vk.api.sdk.VKApiManager
import com.vk.api.sdk.VKApiResponseParser
import com.vk.api.sdk.VKMethodCall
import com.vk.api.sdk.exceptions.VKApiIllegalResponseException
import com.vk.api.sdk.internal.ApiCommand
import org.json.JSONException
import org.json.JSONObject

/**
 * author alex
 */
class GroupsRequest(
    private val cityId: Int
) : ApiCommand<List<Group>>() {
    override fun onExecute(manager: VKApiManager): List<Group> {
        val call = VKMethodCall.Builder()
            .method("groups.search")
            .args("city_id", cityId)
            .args("q", "market")
            .args("market", 1)
            .args("fields", "is_closed")
            .version(manager.config.version)
            .build()
        return manager.execute(call, ResponseApiParser())
    }
}

class CitiesRequest : ApiCommand<List<City>>() {
    override fun onExecute(manager: VKApiManager): List<City> {
        val call = VKMethodCall.Builder()
            .method("database.getCities")
            .args("need_all", 1)
            .args("count", 1000)
            .version(manager.config.version)
            .build()
        return manager.execute(call, ResponseApiParserCities())
    }
}

private class ResponseApiParser : VKApiResponseParser<List<Group>> {
    override fun parse(response: String): List<Group> {
        try {
            val groupsResponse = JSONObject(response).getJSONObject("response")
            val items = groupsResponse.getJSONArray("items")
            val groups = ArrayList<Group>(items.length())
            for (i in 0 until items.length()) {
                val group = Group.parse(items.getJSONObject(i))
                groups.add(group)
            }
            return groups
        } catch (ex: JSONException) {
            throw VKApiIllegalResponseException(ex)
        }
    }
}

private class ResponseApiParserCities : VKApiResponseParser<List<City>> {
    override fun parse(response: String): List<City> {
        try {
            val items = JSONObject(response).getJSONObject("response")
                .getJSONArray("items")
            val cities = ArrayList<City>(items.length())
            for (i in 0 until items.length()) {
                val city = City.parse(items.getJSONObject(i))
                cities.add(city)
            }
            return cities
        } catch (ex: JSONException) {
            throw VKApiIllegalResponseException(ex)
        }
    }
}