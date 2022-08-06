package land.vani.plugin.core

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.ints.shouldBeExactly

@Suppress("unused")
class ExampleTest : DescribeSpec({
    describe("an outer test") {
        it("an inner test") {
            1 + 2 shouldBeExactly 3
        }
        it("an another inner test") {
            3 + 4 shouldBeExactly 7
        }
    }
})
