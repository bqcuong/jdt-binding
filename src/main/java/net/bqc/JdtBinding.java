package net.bqc;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class JdtBinding {

    public static void main(String[] args) throws IOException {
        CompilationUnit cu = getJdtRoot("src/main/java/net/bqc/JdtBinding.java", "src");
        traversal(cu);
    }

    public static void traversal(CompilationUnit cu) {
        cu.accept(new ASTVisitor() {

            @Override
            public boolean visit(TypeDeclaration node) {
                ITypeBinding binding = node.resolveBinding();
                System.out.println(binding.getKey());
                return super.visit(node);
            }

            @Override
            public boolean visit(MethodDeclaration node) {
                IMethodBinding binding = node.resolveBinding();
                System.out.println(binding.getKey());
                return super.visit(node);
            }

            @Override
            public boolean visit(FieldDeclaration node) {
                for (Object f : node.fragments()) {
                    if (f instanceof VariableDeclaration) {
                        VariableDeclaration variable = (VariableDeclaration) f;
                        IVariableBinding binding = variable.resolveBinding();
                        System.out.println(binding.getKey());
                    }
                }
                return false;
            }

        });
    }

    public static CompilationUnit getJdtRoot(String javaFilePath, String sourceFolder) throws IOException {
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setResolveBindings(true);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);

        parser.setBindingsRecovery(true);

        Map options = JavaCore.getOptions();
        parser.setCompilerOptions(options);

        String[] sources = { sourceFolder };

        parser.setEnvironment(null, sources, new String[] { "UTF-8"}, true);

        parser.setUnitName(javaFilePath);
        parser.setSource(new String(Files.readAllBytes(Paths.get(javaFilePath))).toCharArray());
        CompilationUnit cu = (CompilationUnit) parser.createAST(null);
        return cu;
    }
}
