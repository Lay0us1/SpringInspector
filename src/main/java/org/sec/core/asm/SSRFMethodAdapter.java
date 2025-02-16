package org.sec.core.asm;

import org.objectweb.asm.MethodVisitor;
import org.sec.core.asm.base.ParamTaintMethodAdapter;

import java.util.Map;

public class SSRFMethodAdapter extends ParamTaintMethodAdapter {
    private final Map<String, Boolean> pass;

    public SSRFMethodAdapter(int methodArgIndex, Map<String, Boolean> pass, int api, MethodVisitor mv,
                             String owner, int access, String name, String desc) {
        super(methodArgIndex, api, mv, owner, access, name, desc);
        this.pass = pass;
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        boolean urlCondition = owner.equals("java/net/URL") && name.equals("<init>") &&
                desc.equals("(Ljava/lang/String;)V");
        boolean urlOpenCondition = owner.equals("java/net/URL") && name.equals("openConnection") &&
                desc.equals("()Ljava/net/URLConnection;");
        boolean urlInputCondition = owner.equals("java/net/HttpURLConnection") &&
                name.equals("getInputStream") && desc.equals("()Ljava/io/InputStream;");

        boolean apacheHttpInitCondition = owner.equals("org/apache/http/client/methods/HttpGet") &&
                name.equals("<init>") && desc.equals("(Ljava/lang/String;)V");
        boolean apacheHttpExecuteCondition = owner.equals("org/apache/http/impl/client/CloseableHttpClient") &&
                name.equals("execute") && desc.equals("(Lorg/apache/http/client/methods/HttpUriRequest;)" +
                "Lorg/apache/http/client/methods/CloseableHttpResponse;");

        boolean socketInitCondition = owner.equals("java/net/Socket") &&
                name.equals("<init>") && desc.equals("(Ljava/lang/String;I)V");
        boolean socketInputCondition = owner.equals("java/net/Socket") &&
                (name.equals("getInputStream") || name.equals("getOutputStream"));

        boolean okhttpUrlCondition = (owner.equals("okhttp3/Request$Builder") ||
                owner.equals("okhttp/Request$Builder")) &&
                name.equals("url") && (desc.equals("(Ljava/lang/String;)Lokhttp3/Request$Builder;") ||
                desc.equals("(Ljava/lang/String;)Lokhttp/Request$Builder;"));
        boolean okhttpBuildCondition = (owner.equals("okhttp3/Request$Builder") ||
                owner.equals("okhttp/Request$Builder")) &&
                name.equals("build") && (desc.equals("()Lokhttp3/Request;") ||
                desc.equals("()Lokhttp/Request;"));
        boolean okhttpNewCallCondition = (owner.equals("okhttp3/OkHttpClient") ||
                owner.equals("okhttp/OkHttpClient")) &&
                name.equals("newCall") && (desc.equals("(Lokhttp3/Request;)Lokhttp3/Call;") ||
                desc.equals("(Lokhttp/Request;)Lokhttp/Call;"));
        boolean okhttpExecuteCondition = (owner.equals("okhttp3/Call") ||
                owner.equals("okhttp/Call")) && name.equals("execute") &&
                (desc.equals("()Lokhttp3/Response;") || desc.equals("()Lokhttp/Response;"));
        if (urlCondition || urlOpenCondition || apacheHttpInitCondition ||
                okhttpUrlCondition || okhttpBuildCondition || okhttpNewCallCondition) {
            if (operandStack.get(0).contains(true)) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                operandStack.set(0, true);
                return;
            }
        }
        if (urlInputCondition) {
            if (operandStack.get(0).contains(true)) {
                pass.put("JDK", true);
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                return;
            }
        }
        if (apacheHttpExecuteCondition) {
            if (operandStack.get(0).contains(true)) {
                pass.put("APACHE", true);
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                return;
            }
        }

        if (socketInitCondition) {
            if (operandStack.get(0).contains(true) ||
                    operandStack.get(1).contains(true)) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                operandStack.set(0, true);
                return;
            }
        }
        if (socketInputCondition) {
            if (operandStack.get(0).contains(true)) {
                pass.put("SOCKET", true);
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                return;
            }
        }
        if (okhttpExecuteCondition) {
            if (operandStack.get(0).contains(true)) {
                pass.put("OKHTTP", true);
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                return;
            }
        }
        super.visitMethodInsn(opcode, owner, name, desc, itf);
    }
}
