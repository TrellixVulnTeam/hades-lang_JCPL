package hadesc.hir

import hadesc.unit

interface HIRBlockVisitor : TypeVisitor {
    fun visitBlock(block: HIRBlock) {
        block.statements.forEach {
            visitStatement(it)
        }
    }

    fun visitStatement(statement: HIRStatement) {
        return when (statement) {
            is HIRStatement.MatchInt -> visitMatchInt(statement)
            is HIRStatement.Return -> visitReturnStatement(statement)
            is HIRStatement.Store -> visitStore(statement)
            is HIRStatement.Alloca -> visitValDeclaration(statement)
            is HIRStatement.While -> visitWhileStatement(statement)
            is HIRStatement.SwitchInt -> visitConditionalBranchStatement(statement)
            is HIRStatement.Call -> visitCallStatement(statement)
            is HIRStatement.Load -> visitLoadStatement(statement)
            is HIRStatement.GetStructField -> visitGetStructField(statement)
            is HIRStatement.GetStructFieldPointer -> visitGetStructFieldPointer(statement)
            is HIRStatement.Not -> visitNot(statement)
            is HIRStatement.Jump -> visitJump(statement)
            is HIRStatement.IntegerConvert -> visitIntegerConvert(statement)
            is HIRStatement.TypeApplication -> visitTypeApplication(statement)
            is HIRStatement.PointerCast -> visitPointerCast(statement)
            is HIRStatement.BinOp -> visitBinOp(statement)
            is HIRStatement.AllocateClosure -> visitAllocateClosure(statement)
            is HIRStatement.InvokeClosure -> visitInvokeClosureStatement(statement)
            is HIRStatement.Move -> visitMoveStatement(statement)
            is HIRStatement.Memcpy -> visitMemcpyStatement(statement)
        }
    }

    fun visitMemcpyStatement(statement: HIRStatement.Memcpy) {
        visitExpression(statement.source)
        visitExpression(statement.destination)
        visitExpression(statement.bytes)
    }

    fun visitMoveStatement(statement: HIRStatement.Move) = unit

    fun visitInvokeClosureStatement(statement: HIRStatement.InvokeClosure) {
        visitExpression(statement.closureRef)
        statement.args.forEach { visitExpression(it) }
    }

    fun visitAllocateClosure(statement: HIRStatement.AllocateClosure) = unit

    fun visitJump(statement: HIRStatement.Jump) = unit

    fun visitLoadStatement(statement: HIRStatement.Load) {
        visitExpression(statement.ptr)
    }

    fun visitCallStatement(statement: HIRStatement.Call) {
        visitType(statement.resultType)
        visitExpression(statement.callee)
        for (arg in statement.args) {
            visitExpression(arg)
        }
    }

    fun visitConditionalBranchStatement(statement: HIRStatement.SwitchInt) {
        visitExpression(statement.condition)
    }

    fun visitWhileStatement(statement: HIRStatement.While) {
        visitBlock(statement.conditionBlock)
        visitBlock(statement.body)
    }

    fun visitValDeclaration(statement: HIRStatement.Alloca) {
        visitType(statement.type)
    }


    fun visitStore(statement: HIRStatement.Store) {
        visitExpression(statement.ptr)
        visitExpression(statement.value)
    }

    fun visitReturnStatement(statement: HIRStatement.Return) {
        visitExpression(statement.expression)
    }

    fun visitMatchInt(statement: HIRStatement.MatchInt) {
        visitExpression(statement.value)
        for (arm in statement.arms) {
            visitBlock(arm.block)
        }
        visitBlock(statement.otherwise)
    }

    fun visitExpression(expression: HIRExpression) {
        visitType(expression.type)
        return when (expression) {
            is HIRExpression.GlobalRef -> visitGlobalRef(expression)
            is HIRExpression.ParamRef -> visitParamRef(expression)
            is HIRExpression.TraitMethodRef -> visitTraitMethodRef(expression)
            is HIRConstant -> visitConstant(expression)
            is HIRExpression.LocalRef -> visitLocalRef(expression)
        }
    }

    fun visitLocalRef(expression: HIRExpression.LocalRef) = unit

    fun visitBinOp(statement: HIRStatement.BinOp) {
        visitExpression(statement.lhs)
        visitExpression(statement.rhs)
    }

    fun visitGetStructField(expression: HIRStatement.GetStructField) {
        visitType(expression.type)
        visitExpression(expression.lhs)
    }

    fun visitConstant(expression: HIRConstant) = unit

    fun visitGetStructFieldPointer(expression: HIRStatement.GetStructFieldPointer) {
        visitType(expression.type)
        visitExpression(expression.lhs)
    }

    fun visitGlobalRef(expression: HIRExpression.GlobalRef) {

    }

    fun visitIntegerConvert(statement: HIRStatement.IntegerConvert) {
        visitExpression(statement.value)
        visitType(statement.type)
    }

    fun visitNot(statement: HIRStatement.Not) {
        visitExpression(statement.expression)
    }

    fun visitParamRef(expression: HIRExpression.ParamRef) {

    }

    fun visitPointerCast(statement: HIRStatement.PointerCast) {
        visitExpression(statement.value)
        visitType(statement.toPointerOfType)
    }

    fun visitSizeOf(constant: HIRConstant.SizeOf) {
        visitType(constant.ofType)
    }

    fun visitTraitMethodRef(expression: HIRExpression.TraitMethodRef) {
        expression.traitArgs.forEach {
            visitType(it)
        }
    }

    fun visitTypeApplication(statement: HIRStatement.TypeApplication) {
        visitExpression(statement.expression)
        statement.args.forEach { visitType(it) }
    }
}