package hadesc.hir.passes

import hadesc.context.NamingContext

@Suppress("unused")
enum class TypeClass {
    POINTER,
    /**
     * integral types (other than pointers) that fit in 1 register
     */
    INTEGER,
    /**
     * types that fit into vector registers
     */
    SSE,
    /**
     * The class consists of types that fit into a vector register and can be passedand returned in the upper bytes of it.
     */
    SSEUP,
    /**
     * These  classes  consists  of  types  that  will  be  returned  via  the  x87FPU
     */
    X87, X87UP,

    /**
     * This class consists of types that will be returned via the x87FPU
     */
    COMPLEX_X87,

    /**
     * This class is used as initializer in the algorithms. It will be used for
     * padding and empty structures and unions.
     */
    NO_CLASS,

    /**
     * This class consists of types that will be passed and returned in memory via the stack.
     */
    MEMORY,
}