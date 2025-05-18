package com.diploma.client.service

import android.content.Context
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.diploma.client.data.ActionLog
import com.diploma.client.data.Item
import com.diploma.client.data.User
import com.diploma.client.service.model.ItemRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import java.util.UUID

// TODO: Change Static address to Dynamic
const val BASE_URI = "http://192.168.0.105:8080/api/"
private var accessToken = "token"

fun authenticateRequest(
    context: Context,
    username: String,
    password: String,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit
) {
    val queue = Volley.newRequestQueue(context)
    val url = BASE_URI + "auth"

    /*TODO: vvv CLEAR DEBUG CODE vvv*/
    val resp = (0..99).random() >= 30
    if (resp) {
        accessToken = "ACCESS"
        onSuccess("account")
    } else onError("Incorrect login or password")
    return/*TODO: ^^^ CLEAR DEBUG CODE  ^^^*/

    val request = object : StringRequest(Method.POST, url, { response ->
        accessToken = JSONObject(response).getString("accessToken")
        onSuccess(response)
    }, { error ->
        onError(error.message ?: "Incorrect login or password")
    }) {
        override fun getBodyContentType(): String {
            return "application/json; charset=utf-8"
        }

        override fun getBody(): ByteArray {
            val jsonBody = JSONObject()
            jsonBody.put("username", username)
            jsonBody.put("password", password)
            return jsonBody.toString().toByteArray(Charsets.UTF_8)
        }
    }

    queue.add(request)
}

fun changePassword(
    context: Context,
    oldPassword: String,
    newPassword: String,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit
) {
    val queue = Volley.newRequestQueue(context)
    val url = BASE_URI + "users/change-password"

    val request = object : StringRequest(Method.GET, url, { response ->
        onSuccess(response)
    }, { error ->
        onError(error.message ?: "Failed to fetch user")
    }) {
        override fun getHeaders(): Map<String, String> {
            val headers = HashMap<String, String>()
            headers["Authorization"] = "Bearer $accessToken"
            return headers
        }

        override fun getBodyContentType(): String {
            return "application/json; charset=utf-8"
        }

        override fun getBody(): ByteArray {
            val jsonBody = JSONObject()
            jsonBody.put("oldPassword", oldPassword)
            jsonBody.put("newPassword", newPassword)
            return jsonBody.toString().toByteArray(Charsets.UTF_8)
        }
    }
    queue.add(request)
}

fun getUser(
    context: Context,
    username: String? = null,
    id: UUID? = null,
    onSuccess: (User) -> Unit,
    onError: (String) -> Unit
) {
    val queue = Volley.newRequestQueue(context)
    val url = when {
        id != null -> "${BASE_URI}users/${id}"
        username != null -> "${BASE_URI}users?username=${username}"
        else -> return
    }

    val request = object : StringRequest(Method.GET, url, { response ->
        val user = Gson().fromJson(response, User::class.java)
        onSuccess(user)
    }, { error ->
        onError(error.message ?: "Failed to fetch user")
    }) {
        override fun getHeaders(): Map<String, String> {
            val headers = HashMap<String, String>()
            headers["Authorization"] = "Bearer $accessToken"
            return headers
        }
    }

    queue.add(request)
}

fun getLogs(
    context: Context,
    from: String,
    to: String,
    onSuccess: (List<ActionLog>) -> Unit,
    onError: (String) -> Unit
) {
    val queue = Volley.newRequestQueue(context)
    val url = "$BASE_URI/api/logs?from=$from&to=$to"

    val request = object : StringRequest(Method.GET, url, { response ->
        try {
            val type = object : TypeToken<List<ActionLog>>() {}.type
            val logs: List<ActionLog> = Gson().fromJson(response, type)
            onSuccess(logs)
        } catch (e: Exception) {
            onError("Parsing error: ${e.message}")
        }
    }, { error ->
        onError("Bad request: ${error.message ?: "Unknown error"}")
    }) {
        override fun getHeaders(): Map<String, String> {
            val headers = HashMap<String, String>()
            headers["Authorization"] = "Bearer $accessToken"
            return headers
        }
    }

    queue.add(request)
}

fun postItem(
    context: Context,
    item: ItemRequest,
    onSuccess: (Item) -> Unit,
    onError: (String) -> Unit
) {
    val url = "$BASE_URI/api/items"
    val queue = Volley.newRequestQueue(context)

    val jsonBody = JSONObject().apply {
        put("name", item.name)
        put("barcode", item.barcode)
        put("shopId", item.shopId)
    }

    val request = object : JsonObjectRequest(
        Method.POST, url, jsonBody,
        { response ->
            try {
                val itemResponse = Item(
                    id = UUID.fromString(response.optString("id", UUID.randomUUID().toString())),
                    name = response.getString("name"),
                    barcode = response.getString("barcode"),
                    quantity = response.getInt("quantity"),
                    price = response.getDouble("price"),
                    shopId = UUID.fromString(response.getString("shopId")),
                )
                onSuccess(itemResponse)
            } catch (e: Exception) {
                onError("Failed to parse response: ${e.message}")
            }
        },
        { error ->
            onError(error.message ?: "Network error")
        }
    ) {
        override fun getHeaders(): MutableMap<String, String> {
            return mutableMapOf("Authorization" to "Bearer $accessToken")
        }

        override fun getBodyContentType(): String {
            return "application/json; charset=utf-8"
        }
    }

    queue.add(request)
}

fun getItemByUUIDRequest(
    context: Context,
    uuid: String,
    onSuccess: (Item) -> Unit,
    onError: (String) -> Unit
) {
    val url = "$BASE_URI/api/items/$uuid"
    val queue = Volley.newRequestQueue(context)

    val request = object : JsonObjectRequest(
        Method.GET, url, null,
        { response ->
            try {
                val item = Item(
                    id = UUID.fromString(response.optString("id", UUID.randomUUID().toString())),
                    name = response.getString("name"),
                    barcode = response.getString("barcode"),
                    quantity = response.getInt("quantity"),
                    price = response.getDouble("price"),
                    shopId = UUID.fromString(response.getString("shopId"))
                )
                onSuccess(item)
            } catch (e: Exception) {
                onError("Parsing error: ${e.message}")
            }
        },
        { error ->
            onError(error.message ?: "Failed to fetch item")
        }
    ) {
        override fun getHeaders(): MutableMap<String, String> {
            return mutableMapOf("Authorization" to "Bearer $accessToken")
        }
    }

    queue.add(request)
}

fun getItems(
    context: Context,
    shopId: String? = null,
    onSuccess: (List<Item>) -> Unit,
    onError: (String) -> Unit
) {
    val url = if (shopId != null) {
        "$BASE_URI/api/logs?shop_id=$shopId"
    } else {
        "$BASE_URI/api/logs"
    }

    val queue = Volley.newRequestQueue(context)

    val request = object : JsonArrayRequest(
        Method.GET, url, null,
        { response ->
            try {
                val items = mutableListOf<Item>()
                for (i in 0 until response.length()) {
                    val item = response.getJSONObject(i)
                    val itemResponse = Item(
                        id = UUID.fromString(item.optString("id", UUID.randomUUID().toString())),
                        name = item.getString("name"),
                        barcode = item.getString("barcode"),
                        quantity = item.getInt("quantity"),
                        price = item.getDouble("price"),
                        shopId = UUID.fromString(item.getString("shopId")),
                    )
                    items.add(itemResponse)
                }
                onSuccess(items)
            } catch (e: Exception) {
                onError("Parsing error: ${e.message}")
            }
        },
        { error ->
            onError(error.message ?: "Failed to get logs")
        }
    ) {
        override fun getHeaders(): MutableMap<String, String> {
            return mutableMapOf("Authorization" to "Bearer $accessToken")
        }
    }

    queue.add(request)
}

