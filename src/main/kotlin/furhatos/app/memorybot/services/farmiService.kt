package furhatos.app.memorybot.services

import furhatos.app.memorybot.flow.main.tests.*
import furhatos.flow.kotlin.State
import furhatos.nlu.Response
import kotlinx.coroutines.delay

val FARMI_PAGES : Map<State, String> = mapOf(
    LineDrawing to "path",
    CubeDrawing to "image",
    ClockTest to "free",
    AnimalRecognition to "three_images",
    CookieTest to "cookie",
    ReadingTest to "reading"
)

var FARMI_IP : String = "localhost:5000"

fun FARMI_BASE_URL(farmiIp : String = FARMI_IP) : String = "http://$farmiIp"
fun FARMI_START_URL(baseUrl : String = FARMI_BASE_URL()) : String = "${baseUrl}/start_recording"
fun FARMI_STOP_URL(baseUrl : String = FARMI_BASE_URL()) : String = "${baseUrl}/stop_recording"
fun FARMI_PAGE_URL(baseUrl : String = FARMI_BASE_URL()) : String = "${baseUrl}/furhat_page"
fun USER_ACTIVITY_URL(baseUrl : String = FARMI_BASE_URL()) : String = "${baseUrl}/latest_user_interaction"

class FarmiService {
    fun testConnection(baseUrl : String = FARMI_BASE_URL()) : Boolean {
        return httpService.testRequest(USER_ACTIVITY_URL(baseUrl))
    }

    fun startRecording() : Boolean {
        return httpService.getRequest(FARMI_START_URL()).statusCode == 200
    }
    fun stopRecording() : Boolean {
        return httpService.getRequest(FARMI_STOP_URL()).statusCode == 200
    }
    fun gotoPage(state : State) : Boolean {
        this.showScreen()
        return httpService.getRequest("${FARMI_PAGE_URL()}/${FARMI_PAGES[state]}").statusCode == 200
    }
    fun hideScreen() : Boolean {
        return httpService.getRequest("${FARMI_PAGE_URL()}/OFF").statusCode == 200
    }
    fun showScreen() : Boolean {
        return httpService.getRequest("${FARMI_PAGE_URL()}/ON").statusCode == 200
    }
    fun getUserActivity() : khttp.responses.Response {
        return httpService.getRequest(USER_ACTIVITY_URL())
    }
}

val farmiService = FarmiService()