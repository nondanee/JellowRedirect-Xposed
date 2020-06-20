package test.nondanee.jellowredirect;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.util.Map;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class XposedInit implements IXposedHookLoadPackage {
    final static String TAG = "nondanee.jellowRedirect";

    @Override
    public void handleLoadPackage(final LoadPackageParam loadPackageParam) throws Throwable {
        if (!loadPackageParam.packageName.equals("io.iftech.jellow")) return;

        XposedBridge.log("Jellow Redirect Start");

        XposedHelpers.findAndHookMethod(
            "com.ruguoapp.jike.network.e",
            loadPackageParam.classLoader,
            "b",
            String.class,
            new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    String url = (String) param.getResult();
                    String redirect = url
                        .replace("jellow.club", "ruguoapp.com")
                        .replace("/search/topics/posts", "/users/topics/search");
                    param.setResult(redirect);
                    super.afterHookedMethod(param);
                }
            }
        );

        XposedHelpers.findAndHookMethod(
            "com.ruguoapp.jike.network.e",
            loadPackageParam.classLoader,
            "a",
            String.class,
            new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    String url = (String) param.args[0];
                    param.args[0] = url.replace("jellow.club", "ruguoapp.com");
                    super.beforeHookedMethod(param);
                }
            }
        );

        /*
        XposedHelpers.findAndHookMethod(
            "com.ruguoapp.jike.network.d",
            loadPackageParam.classLoader,
            "a",
            new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Map<String, String> headers = (Map) param.getResult();
                    headers.put("App-Version", "7.0.1");
                    headers.put("App-BuildNo", String.valueOf(1048));
                    headers.put("ApplicationId", "com.ruguoapp.jike");
                    headers.put("x-jike-app-id", "XeITUMa6kGKF");
                    param.setResult(headers);
                }
            }
        );
        */

        XposedHelpers.findAndHookMethod(
            "com.ruguoapp.jike.model.api.b1",
            loadPackageParam.classLoader,
            "a",
            String.class,
            String.class,
            new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    String areaCode = (String) param.args[0];
                    String mobilePhoneNumber = (String) param.args[1];

                    Context context = AndroidAppHelper.currentApplication().getApplicationContext();
                    Intent intent = new Intent(context, loadPackageParam.classLoader.loadClass("com.ruguoapp.jike.business.login.newui.LoginWithCodeActivity"));
                    intent.putExtra("countryCode", areaCode);
                    intent.putExtra("phone", mobilePhoneNumber);

                    XposedHelpers.callStaticMethod(loadPackageParam.classLoader.loadClass("com.ruguoapp.jike.global.j"), "a", context, intent);
                }
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    /*
                    Object response = param.getResult();
                    Log.d(TAG, "afterHookedMethod(isAllowLoginJellow): " + response.toString());
                    boolean success1 = (boolean) XposedHelpers.callMethod(response, "getSuccess");
                    XposedHelpers.callMethod(response, "setSuccess", true);
                    boolean success2 = (boolean) XposedHelpers.callMethod(response, "getSuccess");
                    Log.d(TAG, "afterHookedMethod isAllowLoginJellow(): success1" + success1 + " success2" + success2);
                    param.setResult(response);
                    */
                    super.afterHookedMethod(param);
                }
            }
        );

        XposedHelpers.findAndHookMethod(
            "com.ruguoapp.jike.business.login.newui.b$b$b",
            loadPackageParam.classLoader,
            "a",
            Throwable.class,
            new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    param.setResult(null); // preventDefault
                }
            }
        );
    }
}
