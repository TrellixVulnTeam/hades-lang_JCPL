package llvm

import org.bytedeco.javacpp.SizeTPointer
import org.bytedeco.llvm.LLVM.LLVMMetadataRef
import org.bytedeco.llvm.LLVM.LLVMModuleRef
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

private typealias M = LLVMModuleRef

fun M.addGlobal(name: String, type: Type): Value {
    return LLVM.LLVMAddGlobal(this, type, name)
}

fun M.addModuleFlag(key: String, value: LLVMMetadataRef, flags: Int = LLVM.LLVMModuleFlagBehaviorError) =
    LLVM.LLVMAddModuleFlag(this, flags, key, key.length.toLong(), value)

fun M.getSourceFileName(): String = LLVM.LLVMGetSourceFileName(this, SizeTPointer(0)).string

val M.ref get() = this

fun M.getFunction(name: String): Value? {
    return LLVM.LLVMGetNamedFunction(this, name)
}

fun M.getNamedGlobal(name: String): Value? {
    return LLVM.LLVMGetNamedGlobal(this, name)
}

fun M.addFunction(name: String, type: Type): LLVMValueRef = LLVM.LLVMAddFunction(this, name, type)

fun M.prettyPrint(): String =
    LLVM.LLVMPrintModuleToString(this).string

fun M.getIntrinsicDeclaration(name: String, paramTypes: List<Type>): LLVMValueRef {
    return LLVM.LLVMGetIntrinsicDeclaration(
        this,
        LLVMUtils.lookupIntrinsicID(name),
        paramTypes.asPointerPointer(),
        paramTypes.size.toLong()
    )
}
