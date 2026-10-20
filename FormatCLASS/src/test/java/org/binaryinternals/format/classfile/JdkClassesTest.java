package org.binaryinternals.format.classfile;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.classfile.ClassModel;
import java.lang.classfile.CodeElement;
import java.lang.classfile.Instruction;
import java.lang.classfile.Label;
import java.lang.classfile.MethodModel;
import java.lang.classfile.attribute.CodeAttribute;
import java.lang.classfile.constantpool.PoolEntry;
import java.lang.classfile.constantpool.Utf8Entry;
import java.lang.classfile.instruction.BranchInstruction;
import java.lang.classfile.instruction.IncrementInstruction;
import java.lang.classfile.instruction.LookupSwitchInstruction;
import java.lang.classfile.instruction.SwitchCase;
import java.lang.classfile.instruction.TableSwitchInstruction;
import java.lang.reflect.ClassFileFormatVersion;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.ToIntFunction;
import org.binaryinternals.commonlib.ui.JTreeNodeFileComponent;
import org.binaryinternals.format.classfile.constant.CONSTANT_Utf8_info;
import org.junit.jupiter.api.Test;

/// Parses every class of the running JDK's `java.base` module, and cross-checks
/// the result with the JDK's own Class-File API (`java.lang.classfile`).
class JdkClassesTest {

    @Test
    void corpusIsComplete() {
        final int count = TestSupport.jdkClasses().size();
        System.out.println("java.base classes: " + count);
        assertTrue(count > 5000, "java.base should have thousands of classes, found " + count);
    }

    @Test
    void everyClassParsesAndBuildsTree() {
        final List<String> failures = new ArrayList<>();
        for (Path p : TestSupport.jdkClasses()) {
            try {
                final byte[] bytes = TestSupport.read(p);
                final ClassFile cf = TestSupport.parse(bytes);
                for (JTreeNodeFileComponent node : TestSupport.treeNodes(cf)) {
                    if (node.getLastPosPlus1() > bytes.length) {
                        failures.add(p + ": node out of file bounds: " + node.getText());
                    }
                    if (node.getDescription() != null && node.getDescription().startsWith("msg_")) {
                        failures.add(p + ": message key shown as description: " + node.getDescription());
                    }
                }
            } catch (Exception | AssertionError e) {
                failures.add(p + ": " + e);
            }
        }
        assertTrue(failures.isEmpty(), () -> TestSupport.summary(failures));
    }

    @Test
    void everyNodeIsInsideItsParent() throws Exception {
        final List<String> failures = new ArrayList<>();
        for (Path p : TestSupport.jdkClasses()) {
            for (String outside : TestSupport.nodesOutsideParent(TestSupport.parse(TestSupport.read(p)))) {
                failures.add(p + ": " + outside);
            }
        }
        assertTrue(failures.isEmpty(), () -> TestSupport.summary(failures));
    }

    @Test
    void everyClassVerifies() throws Exception {
        final List<String> failures = new ArrayList<>();
        for (Path p : TestSupport.jdkClasses()) {
            final ClassFile.Verification result = TestSupport.parse(TestSupport.read(p)).verify();
            if (!"passed".equals(result.summary())) {
                failures.add(p + ": " + result);
            }
        }
        assertTrue(failures.isEmpty(), () -> TestSupport.summary(failures));
    }

    @Test
    void versionMatchesClassFileApi() throws Exception {
        final List<String> failures = new ArrayList<>();
        for (Path p : TestSupport.jdkClasses()) {
            final byte[] bytes = TestSupport.read(p);
            final ClassModel jdk = java.lang.classfile.ClassFile.of().parse(bytes);
            final ClassFileFormatVersion expected = ClassFileFormatVersion.fromMajor(jdk.majorVersion());
            final ClassFile ours = TestSupport.parse(bytes);
            if (!ours.getVersion().equals(java.util.Optional.of(expected))) {
                failures.add(p + ": expected " + expected + " but was " + ours.getVersion());
            }
        }
        assertTrue(failures.isEmpty(), () -> TestSupport.summary(failures));
    }

    @Test
    void utf8ConstantsMatchClassFileApi() throws Exception {
        final List<String> failures = new ArrayList<>();
        for (Path p : TestSupport.jdkClasses()) {
            final byte[] bytes = TestSupport.read(p);
            final ClassModel jdk = java.lang.classfile.ClassFile.of().parse(bytes);
            final ClassFile ours = TestSupport.parse(bytes);
            for (PoolEntry e : jdk.constantPool()) {
                if (e instanceof Utf8Entry u
                        && ours.constant_pool[e.index()] instanceof CONSTANT_Utf8_info mine
                        && !u.stringValue().equals(mine.getValue())) {
                    failures.add(p + " #" + e.index());
                }
            }
        }
        assertTrue(failures.isEmpty(), () -> TestSupport.summary(failures));
    }

    /// Compares branch targets, switch targets and `iinc` constants of every
    /// method with the Class-File API.
    @Test
    void instructionsMatchClassFileApi() throws Exception {
        final List<String> failures = new ArrayList<>();
        for (Path p : TestSupport.jdkClasses()) {
            final ClassModel jdk = java.lang.classfile.ClassFile.of().parse(TestSupport.read(p));
            for (MethodModel m : jdk.methods()) {
                if (m.code().orElse(null) instanceof CodeAttribute code) {
                    checkCode(p + " " + m.methodName() + m.methodType(), code, failures);
                }
            }
        }
        assertTrue(failures.isEmpty(), () -> TestSupport.summary(failures));
    }

    private static void checkCode(final String where, final CodeAttribute code, final List<String> failures) {
        final Map<Integer, Opcode.InstructionParsed> ours = new HashMap<>();
        for (Opcode.InstructionParsed ip : Opcode.parseCode(code.codeArray())) {
            ours.put(ip.offset, ip);
        }
        final ToIntFunction<Label> bci = code::labelToBci;

        int pos = 0;
        for (CodeElement element : code) {
            if (!(element instanceof Instruction ins)) {
                continue;
            }
            final Opcode.InstructionParsed mine = ours.get(pos);
            final String at = where + " bci=" + pos + " " + ins.opcode() + ": ";
            if (mine == null) {
                failures.add(at + "no instruction parsed");
            } else {
                switch (ins) {
                    case BranchInstruction b -> expect(failures, at, bci.applyAsInt(b.target()), mine.getAbsoluteBranchByte());
                    case IncrementInstruction i -> expect(failures, at, i.constant(), mine.getImmediateValue());
                    case TableSwitchInstruction t -> {
                        expect(failures, at, bci.applyAsInt(t.defaultTarget()) - pos, mine.getTableSwitch().defaultbyte());
                        for (SwitchCase c : t.cases()) {
                            expect(failures, at + "case " + c.caseValue() + " ", bci.applyAsInt(c.target()) - pos, mine.getTableSwitch().jumpoffsets().get(c.caseValue()));
                        }
                    }
                    case LookupSwitchInstruction l -> {
                        expect(failures, at, bci.applyAsInt(l.defaultTarget()) - pos, mine.getLookupSwitch().defaultbyte());
                        for (SwitchCase c : l.cases()) {
                            expect(failures, at + "case " + c.caseValue() + " ", bci.applyAsInt(c.target()) - pos, mine.getLookupSwitch().mapoffsets().get(c.caseValue()));
                        }
                    }
                    default -> {
                        // Other instructions are covered by everyClassParsesAndBuildsTree
                    }
                }
            }
            pos += ins.sizeInBytes();
        }
    }

    private static void expect(final List<String> failures, final String at, final int expected, final Integer actual) {
        if (!Objects.equals(expected, actual)) {
            failures.add(at + "expected " + expected + " but was " + actual);
        }
    }
}
