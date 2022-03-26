package hadesc.hir

import hadesc.location.SourceLocation
import hadesc.types.Type

sealed interface HIRConstant: HIROperand {

    data class ByteString(
            override val location: SourceLocation,
            override val type: Type,
            val bytes: ByteArray
    ) : HIRConstant

    data class BoolValue(
            override val location: SourceLocation,
            override val type: Type,
            val value: Boolean
    ) : HIRConstant

    data class IntValue(
            override val location: SourceLocation,
            override val type: Type,
            val value: Int
    ) : HIRConstant

    data class FloatValue(
        override val location: SourceLocation,
        override val type: Type.FloatingPoint,
        val value: Double
    ) : HIRConstant

    data class ArrayLiteral(
        override val location: SourceLocation,
        override val type: Type.Array,
        val items: List<HIRConstant>,
    ) : HIRConstant

    data class Void(override val location: SourceLocation): HIRConstant {
        override val type: Type
            get() = Type.Void
    }
}