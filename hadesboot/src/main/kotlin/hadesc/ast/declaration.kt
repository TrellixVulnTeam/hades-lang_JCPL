package hadesc.ast

import hadesc.Name
import hadesc.location.HasLocation
import hadesc.location.SourceLocation

sealed interface Declaration : HasLocation {
    /**
     * Location of the first few tokens for error reporting
     * Useful for larger declarations where we wan't to report
     * an error for the entire declaration but the full declaration
     * is just to noisy of an error span. For example, an error like,
     * "Only function defs are allowed inside extensions" should ideally
     * only highlight the first token or two for a struct declaration.
     */
    open val startLoc get() = location

    data class Error(override val location: SourceLocation) : Declaration
    data class ImportAs(
        val modulePath: QualifiedPath,
        val asName: Binder
    ) : Declaration {
        override val location: SourceLocation
            get() = SourceLocation.between(modulePath, asName)

        override val startLoc: SourceLocation
            get() = modulePath.location
    }
    data class ImportMembers(
        override val location: SourceLocation,
        val modulePath: QualifiedPath,
        val names: List<Binder>,
    ) : Declaration

    data class FunctionDef(
        override val location: SourceLocation,
        val externName: Identifier?,
        val signature: FunctionSignature,
        val body: Block
    ) : Declaration, ScopeTree {
        val name get() = signature.name
        val typeParams get() = signature.typeParams
        val params get() = signature.params
        override val startLoc: SourceLocation
            get() = signature.name.location
    }

    data class ConstDefinition(
        override val location: SourceLocation,
        val name: Binder,
        val annotation: TypeAnnotation?,
        val initializer: Expression
    ) : Declaration {
        override val startLoc: SourceLocation
            get() = name.location
    }

    data class ExternFunctionDef(
        override val location: SourceLocation,
        val binder: Binder,
        val paramTypes: List<TypeAnnotation>,
        val returnType: TypeAnnotation,
        val externName: Identifier
    ) : Declaration {
        override val startLoc: SourceLocation
            get() = binder.location
    }

    data class ExternConst(
        override val location: SourceLocation,
        val name: Binder,
        val type: TypeAnnotation,
        val externName: Identifier,
    ) : Declaration

    data class Struct(
        override val location: SourceLocation,
        val decorators: List<Decorator>,
        val binder: Binder,
        val typeParams: List<TypeParam>? = null,
        val members: List<Member>
    ) : Declaration, ScopeTree {
        override val startLoc: SourceLocation
            get() = binder.location

        sealed class Member {
            data class Field(
                val binder: Binder,
                val isMutable: Boolean,
                val typeAnnotation: TypeAnnotation
            ) : Member()
        }
    }

    data class TypeAlias(
            override val location: SourceLocation,
            val name: Binder,
            val typeParams: List<TypeParam>?,
            val rhs: TypeAnnotation
    ) : Declaration, ScopeTree

    data class ExtensionDef(
        override val location: SourceLocation,
        val name: Binder,
        val typeParams: List<TypeParam>?,
        val forType: TypeAnnotation,
        val whereClause: WhereClause?,
        val declarations: List<Declaration>,
    ) : Declaration, ScopeTree {
        val functionDefs get(): List<FunctionDef> = declarations.filterIsInstance<FunctionDef>()
    }

    data class TraitDef(
            override val location: SourceLocation,
            val name: Binder,
            val params: List<TypeParam>,
            val members: List<TraitMember>
    ) : Declaration, ScopeTree {

        val signatures get() = members.filterIsInstance<TraitMember.Function>().map { it.signature }

        fun hasAssociatedType(name: Identifier): Boolean {
            return members.filterIsInstance<TraitMember.AssociatedType>()
                .any { it.binder.identifier.name == name.name }
        }
    }

    sealed class TraitMember: HasLocation {
        data class Function(
            val signature: FunctionSignature
        ) : TraitMember() {
            override val location get() = signature.location
        }

        data class AssociatedType(
            val binder: Binder
        ) : TraitMember() {
            override val location get() = binder.location
        }
    }

    data class ImplementationDef(
            override val location: SourceLocation,
            val typeParams: List<TypeParam>?,
            val traitRef: QualifiedPath,
            val traitArguments: List<TypeAnnotation>,
            val whereClause: WhereClause?,
            val body: List<Declaration>,
    ) : Declaration, ScopeTree

    data class Enum(
        override val location: SourceLocation,
        val decorators: List<Decorator>,
        val name: Binder,
        val typeParams: List<TypeParam>?,
        val cases: List<Case>,
    ): Declaration, ScopeTree {
        fun getCase(name: Name): Pair<Case, Int>? {
            var result: Pair<Case, Int>? = null
            cases.forEachIndexed { index, case ->
                if (case.name.name == name) {
                    result = case to index
                    return@forEachIndexed
                }
            }
            return result
        }

        data class Case(
            val name: Binder,
            val params: List<EnumCaseParam>?,
        )
    }
}

data class EnumCaseParam(val binder: Binder?, val annotation: TypeAnnotation)


