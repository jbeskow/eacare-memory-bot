package furhatos.app.memorybot.services

import khttp.responses.Response
import java.lang.Exception

class HttpService {
    fun testRequest(url : String) : Boolean {
        return try {
            val res = khttp.get(url)
            res.statusCode == 200
        }
        catch (e : Exception) {
            false
        }
    }
    fun getRequest(url : String) : Response {
        val res = khttp.get(url)
        println("Request $url : ${res.statusCode}")
        return res
    }
}

val httpService = HttpService()