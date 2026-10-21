package org.binaryinternals.format.classfile;

import static java.lang.classfile.ClassFile.ACC_FINAL;
import static java.lang.classfile.ClassFile.ACC_STATIC;
import static java.lang.constant.ConstantDescs.CD_String;
import static java.lang.constant.ConstantDescs.CD_int;
import static java.lang.constant.ConstantDescs.CD_void;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.classfile.Annotation;
import java.lang.classfile.AnnotationElement;
import java.lang.classfile.AnnotationValue;
import java.lang.classfile.Label;
import java.lang.classfile.attribute.CodeAttribute;
import java.lang.classfile.attribute.ConstantValueAttribute;
import java.lang.classfile.attribute.MethodParameterInfo;
import java.lang.classfile.attribute.ModuleAttribute;
import java.lang.classfile.attribute.MethodParametersAttribute;
import java.lang.classfile.attribute.PermittedSubclassesAttribute;
import java.lang.classfile.attribute.RuntimeVisibleAnnotationsAttribute;
import java.lang.classfile.attribute.RuntimeVisibleParameterAnnotationsAttribute;
import java.lang.classfile.constantpool.ClassEntry;
import java.lang.classfile.constantpool.ConstantPoolBuilder;
import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;
import java.lang.constant.ModuleDesc;
import java.lang.reflect.ClassFileFormatVersion;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.format.classfile.constant.CONSTANT_Utf8_info;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/// Edge cases which the JDK's own classes do not cover, built with the
/// Class-File API.
class GeneratedClassesTest {

    private static final MethodTypeDesc VOID_INT = MethodTypeDesc.of(CD_void, CD_int);

    @Test
    void modifiedUtf8IsDecoded() throws Exception {
        final String value = "a\u0000b😀";
        final ClassFile cf = TestSupport.parse(TestSupport.build(cb -> cb.withField("s", CD_String,
                fb -> fb.withFlags(ACC_STATIC | ACC_FINAL).with(ConstantValueAttribute.of(value)))));

        assertTrue(Arrays.stream(cf.constant_pool)
                .anyMatch(cp -> cp instanceof CONSTANT_Utf8_info u && u.getValue().equals(value)));
    }

    @Test
    void lastConstantIsLong() throws Exception {
        final ConstantPoolBuilder pool = ConstantPoolBuilder.of();
        final ClassEntry thisClass = pool.classEntry(ClassDesc.of("test.Sample"));
        final ClassEntry superClass = pool.classEntry(ConstantDescs.CD_Object);
        pool.longEntry(42L);
        final ClassFile cf = TestSupport.parse(java.lang.classfile.ClassFile.of().build(thisClass, pool,
                cb -> cb.withSuperclass(superClass)));

        assertNull(cf.constant_pool[cf.constant_pool.length - 1], "precondition: the long occupies the last two slots");
        assertTrue(TestSupport.treeTexts(cf).stream().anyMatch(t -> t.startsWith("constant_pool[")));
    }

    @Test
    void unnamedMethodParameters() throws Exception {
        final ClassFile cf = TestSupport.parse(TestSupport.build(cb -> cb.withMethod("m",
                MethodTypeDesc.of(CD_void, CD_int, CD_int), ACC_STATIC, mb -> mb
                        .with(MethodParametersAttribute.of(
                                MethodParameterInfo.of(Optional.empty(), 0),
                                MethodParameterInfo.ofParameter(Optional.of("b"), ACC_FINAL)))
                        .withCode(cob -> cob.return_()))));

        final List<JTreeNodeFileComponent> params = TestSupport.treeNodes(cf).stream()
                .filter(n -> n.getText().startsWith("parameter "))
                .toList();
        assertEquals(2, params.size());
        assertTrue(params.get(1).getStartPos() > params.get(0).getStartPos(), "each parameter has its own position");
    }

    @Test
    void permittedSubclasses() throws Exception {
        final ClassFile cf = TestSupport.parse(TestSupport.build(cb -> cb
                .with(PermittedSubclassesAttribute.ofSymbols(ClassDesc.of("test.A"), ClassDesc.of("test.B")))));

        assertTrue(TestSupport.treeTexts(cf).contains("classes [2]"));
    }

    @Test
    void unsignedByteCounts() throws Exception {
        final ClassDesc[] params = new ClassDesc[200];
        Arrays.fill(params, CD_int);
        final ClassFile cf = TestSupport.parse(TestSupport.build(cb -> cb.withMethod("m",
                MethodTypeDesc.of(CD_void, params), ACC_STATIC, mb -> mb
                        .with(RuntimeVisibleParameterAnnotationsAttribute.of(
                                Collections.nCopies(200, List.of())))
                        .withCode(cob -> cob.return_()))));

        assertTrue(TestSupport.treeTexts(cf).contains("num_parameters: 200"));
    }

    @Test
    void annotationValuesAreInTree() throws Exception {
        final ClassFile cf = TestSupport.parse(TestSupport.build(cb -> cb.with(RuntimeVisibleAnnotationsAttribute.of(
                Annotation.of(ClassDesc.of("test.Anno"),
                        AnnotationElement.of("e", AnnotationValue.ofEnum(ClassDesc.of("test.E"), "X")),
                        AnnotationElement.of("a", AnnotationValue.ofArray(AnnotationValue.ofInt(1), AnnotationValue.ofInt(2))),
                        AnnotationElement.of("n", AnnotationValue.ofAnnotation(Annotation.of(ClassDesc.of("test.Inner")))),
                        AnnotationElement.ofClass("c", ClassDesc.of("test.C")))))));

        final List<String> texts = TestSupport.treeTexts(cf);
        assertEquals(List.of(), TestSupport.nodesOutsideParent(cf));
        assertTrue(texts.stream().anyMatch(t -> t.startsWith("type_name_index:")), "enum value");
        assertTrue(texts.stream().anyMatch(t -> t.startsWith("const_name_index:")), "enum value");
        assertTrue(texts.stream().anyMatch(t -> t.startsWith("num_values:")), "array value");
        assertTrue(texts.stream().anyMatch(t -> t.startsWith("class_info_index:")), "class value");
        assertEquals(2, texts.stream().filter(t -> t.startsWith("type_index:")).count(), "outer and nested annotation");
    }

    @Test
    void previewVersion() throws Exception {
        final ClassFile cf = TestSupport.parse(TestSupport.build(cb -> cb.withVersion(69, 0xFFFF)));

        assertEquals(Optional.of(ClassFileFormatVersion.RELEASE_25), cf.getVersion());
        assertTrue(cf.isPreview());
        assertEquals("69.65535 (Java SE 25, preview features)", cf.getVersionText());
    }

    @Test
    void unknownFutureVersion() throws Exception {
        final ClassFile cf = TestSupport.parse(TestSupport.build(cb -> cb.withVersion(99, 0)));

        assertEquals(Optional.empty(), cf.getVersion());
        assertFalse(cf.isPreview());
        assertEquals("99.0 (unknown Java SE version)", cf.getVersionText());
        TestSupport.treeTexts(cf);
    }

    @Test
    void signedBranchAndIncrement() throws Exception {
        final byte[] code = codeOf(cob -> {
            final Label top = cob.newBoundLabel();
            cob.iinc(0, -300).iinc(0, -5).aconst_null().ifnull(top).return_();
        });
        final List<Opcode.InstructionParsed> parsed = Opcode.parseCode(code);

        assertEquals("wide iinc index = 0 const = -300", parsed.get(0).getOpcodeText());
        assertEquals(-300, parsed.get(0).getImmediateValue());
        assertEquals(-5, parsed.get(1).getImmediateValue());
        assertEquals("ifnull", parsed.get(3).getOpcodeText());
        assertEquals(0, parsed.get(3).getAbsoluteBranchByte(), "backward branch to the start");
    }

    @Test
    void wideOpcodeNameIsPerInstruction() throws Exception {
        final List<Opcode.InstructionParsed> parsed = Opcode.parseCode(codeOf(cob -> cob
                .iinc(0, 1000).iload(0).istore(300).iinc(0, 2000).return_()));

        assertEquals("wide iinc", parsed.get(0).opCodeName);
        assertEquals("wide istore", parsed.get(2).opCodeName);
        assertEquals("wide iinc", parsed.get(3).opCodeName);
    }

    @Test
    void openModule() throws Exception {
        final ClassFile cf = TestSupport.parse(java.lang.classfile.ClassFile.of().buildModule(
                ModuleAttribute.of(ModuleDesc.of("m"), mb -> mb.moduleFlags(java.lang.reflect.AccessFlag.OPEN))));

        assertTrue(TestSupport.treeTexts(cf).stream().anyMatch(t -> t.startsWith("module_flags:") && t.endsWith(" - open")),
                () -> TestSupport.treeTexts(cf).stream().filter(t -> t.startsWith("module_flags")).toList().toString());
    }

    @Test
    void verificationPasses() throws Exception {
        final ClassFile cf = TestSupport.parse(TestSupport.build(cb -> cb
                .withMethodBody("m", MethodTypeDesc.of(CD_void), ACC_STATIC, cob -> cob.return_())));

        assertEquals("passed", cf.verify().summary());
        assertTrue(TestSupport.treeTexts(cf).contains("verification: passed"));
    }

    @Test
    void verificationReportsErrors() throws Exception {
        final byte[] bytes = java.lang.classfile.ClassFile.of(java.lang.classfile.ClassFile.StackMapsOption.DROP_STACK_MAPS)
                .build(ClassDesc.of("test.Sample"), cb -> cb.withMethodBody("m", MethodTypeDesc.of(ConstantDescs.CD_Object),
                        ACC_STATIC, cob -> cob.iconst_0().areturn()));
        final ClassFile.Verification result = TestSupport.parse(bytes).verify();

        assertFalse(result.errors().isEmpty(), result.toString());
        assertEquals(result.errors().size() + " error(s)", result.summary());
    }

    @Test
    void verificationResolvesClassesFromClassPathRoot(@TempDir final Path root) throws Exception {
        final ClassDesc base = ClassDesc.of("p.Base");
        final ClassDesc child = ClassDesc.of("p.Child");
        final Path dir = Files.createDirectories(root.resolve("p"));
        Files.write(dir.resolve("Base.class"), java.lang.classfile.ClassFile.of().build(base, cb -> { }));
        Files.write(dir.resolve("Child.class"), java.lang.classfile.ClassFile.of().build(child, cb -> cb
                .withSuperclass(base)
                .withMethodBody("up", MethodTypeDesc.of(base), ACC_STATIC, cob -> cob
                        .new_(child).dup().invokespecial(child, "<init>", ConstantDescs.MTD_void).areturn())));

        assertEquals("passed", new ClassFile(dir.resolve("Child.class")).verify().summary());

        Files.delete(dir.resolve("Base.class"));
        final ClassFile.Verification incomplete = new ClassFile(dir.resolve("Child.class")).verify();
        assertEquals(List.of(), incomplete.errors());
        assertEquals("incomplete, 1 class(es) not found", incomplete.summary());
    }

    private static byte[] codeOf(final Consumer<java.lang.classfile.CodeBuilder> code) {
        final byte[] bytes = TestSupport.build(cb -> cb.withMethodBody("m", VOID_INT, ACC_STATIC, code));
        return java.lang.classfile.ClassFile.of().parse(bytes).methods().getFirst().code()
                .map(c -> ((CodeAttribute) c).codeArray()).orElseThrow();
    }
}
