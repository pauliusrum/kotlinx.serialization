package kotlinx.serialization

import kotlinx.serialization.json.*
import kotlin.test.*

class JsonAstTest {
    @Test
    fun parseWithoutExceptions() {
        val input = """{"a": "foo",              "b": 10, "c": ["foo", 100500, {"bar": "baz"}]}"""
        val elem = JsonAstReader(input).readFully()
    }

    @Test
    fun jsonValue() {
        val v = JsonValue("foo")
        assertEquals(v, JsonAstReader("foo").readFully())
    }

    @Test
    fun jsonObject() {
        val input = """{"a": "foo","b": 10, "c": true, "d": null}"""
        val elem = JsonAstReader(input).readFully()

        assertTrue(elem is JsonObject)
        elem as JsonObject
        assertEquals(setOf("a", "b", "c", "d"), elem.keys)

        assertEquals(JsonValue("foo"), elem["a"])
        assertEquals(10, elem.getAsValue("b")?.asInt)
        assertEquals(true, elem.getAsValue("c")?.asBoolean)
        assertTrue(elem.getValue("d") === JsonNull)
    }

    @Test
    fun jsonObjectWithArrays() {
        val input = """{"a": "foo",              "b": 10, "c": ["foo", 100500, {"bar": "baz"}]}"""
        val elem = JsonAstReader(input).readFully()

        assertTrue(elem is JsonObject)
        elem as JsonObject
        assertEquals(setOf("a", "b", "c"), elem.keys)
        assertTrue(elem.getValue("c") is JsonArray)

        val array = elem.getAsArray("c")!!
        assertEquals("foo", array.getAsValue(0)?.str)
        assertEquals(100500, array.getAsValue(1)?.asInt)

        assertTrue(array[2] is JsonObject)
        val third = array.getAsObject(2)!!
        assertEquals("baz", third.getAsValue("bar")?.str)
    }
}
