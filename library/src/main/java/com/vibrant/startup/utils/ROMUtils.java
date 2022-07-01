package com.vibrant.startup.utils;

import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public final class ROMUtils {

    /* renamed from: A */
    private static final String f18567A = "ro.build.MiFavor_version";

    /* renamed from: B */
    private static final String f18568B = "ro.rom.version";

    /* renamed from: C */
    private static final String f18569C = "ro.build.rom.id";

    /* renamed from: D */
    private static final String f18570D = "unknown";

    /* renamed from: E */
    private static C5526a f18571E = null;

    /* renamed from: a */
    private static final String[] f18572a = {"huawei"};

    /* renamed from: b */
    private static final String[] f18573b = {"vivo"};

    /* renamed from: c */
    private static final String[] f18574c = {"xiaomi"};

    /* renamed from: d */
    private static final String[] f18575d = {"oppo"};

    /* renamed from: e */
    private static final String[] f18576e = {"leeco", "letv"};

    /* renamed from: f */
    private static final String[] f18577f = {"360", "qiku"};

    /* renamed from: g */
    private static final String[] f18578g = {"zte"};

    /* renamed from: h */
    private static final String[] f18579h = {"oneplus"};

    /* renamed from: i */
    private static final String[] f18580i = {"nubia"};

    /* renamed from: j */
    private static final String[] f18581j = {"coolpad", "yulong"};

    /* renamed from: k */
    private static final String[] f18582k = {"lg", "lge"};

    /* renamed from: l */
    private static final String[] f18583l = {"google"};

    /* renamed from: m */
    private static final String[] f18584m = {"samsung"};

    /* renamed from: n */
    private static final String[] f18585n = {"meizu"};

    /* renamed from: o */
    private static final String[] f18586o = {"lenovo"};

    /* renamed from: p */
    private static final String[] f18587p = {"smartisan", "deltainno"};

    /* renamed from: q */
    private static final String[] f18588q = {"htc"};

    /* renamed from: r */
    private static final String[] f18589r = {"sony"};

    /* renamed from: s */
    private static final String[] f18590s = {"gionee", "amigo"};

    /* renamed from: t */
    private static final String[] f18591t = {"motorola"};

    /* renamed from: u */
    private static final String f18592u = "ro.build.version.emui";

    /* renamed from: v */
    private static final String f18593v = "ro.vivo.os.build.display.id";

    /* renamed from: w */
    private static final String f18594w = "ro.build.version.incremental";

    /* renamed from: x */
    private static final String f18595x = "ro.build.version.opporom";

    /* renamed from: y */
    private static final String f18596y = "ro.letv.release.version";

    /* renamed from: z */
    private static final String f18597z = "ro.build.uiversion";

    /* renamed from: com.y.f.l.k.e.v$a */
    /* compiled from: ROMUtils */
    public static class C5526a {

        /* renamed from: a */
        private String f18598a;

        /* renamed from: b */
        private String f18599b;

        public String toString() {
            return "RomInfo{name=" + this.f18598a + ", version=" + this.f18599b + "}";
        }

        /* renamed from: b */
        public String mo39775b() {
            return this.f18599b;
        }

        /* renamed from: a */
        public String mo39774a() {
            return this.f18598a;
        }
    }

    private ROMUtils() {
    }

    /* renamed from: a */
    public static C5526a m22535a() {
        C5526a aVar = f18571E;
        if (aVar != null) {
            return aVar;
        }
        C5526a v = m22562v();
        f18571E = v;
        return v;
    }

    /* renamed from: b */
    public static boolean m22539b() {
        return f18577f[0].equals(m22535a().f18598a);
    }

    /* renamed from: c */
    public static boolean m22541c() {
        return f18581j[0].equals(m22535a().f18598a);
    }

    /* renamed from: d */
    public static boolean m22543d() {
        return f18590s[0].equals(m22535a().f18598a);
    }

    /* renamed from: e */
    public static boolean m22545e() {
        return f18583l[0].equals(m22535a().f18598a);
    }

    /* renamed from: f */
    public static boolean m22546f() {
        return f18588q[0].equals(m22535a().f18598a);
    }

    /* renamed from: g */
    public static boolean m22547g() {
        return f18572a[0].equals(m22535a().f18598a);
    }

    /* renamed from: h */
    public static boolean m22548h() {
        return f18576e[0].equals(m22535a().f18598a);
    }

    /* renamed from: i */
    public static boolean m22549i() {
        return f18586o[0].equals(m22535a().f18598a);
    }

    /* renamed from: j */
    public static boolean m22550j() {
        return f18582k[0].equals(m22535a().f18598a);
    }

    /* renamed from: k */
    public static boolean m22551k() {
        return f18585n[0].equals(m22535a().f18598a);
    }

    /* renamed from: l */
    public static boolean m22552l() {
        return f18591t[0].equals(m22535a().f18598a);
    }

    /* renamed from: m */
    public static boolean m22553m() {
        return f18580i[0].equals(m22535a().f18598a);
    }

    /* renamed from: n */
    public static boolean m22554n() {
        return f18579h[0].equals(m22535a().f18598a);
    }

    /* renamed from: o */
    public static boolean m22555o() {
        return f18575d[0].equals(m22535a().f18598a);
    }

    /* renamed from: p */
    public static boolean m22556p() {
        return f18584m[0].equals(m22535a().f18598a);
    }

    /* renamed from: q */
    public static boolean m22557q() {
        return f18587p[0].equals(m22535a().f18598a);
    }

    /* renamed from: r */
    public static boolean m22558r() {
        return f18589r[0].equals(m22535a().f18598a);
    }

    /* renamed from: s */
    public static boolean m22559s() {
        return f18573b[0].equals(m22535a().f18598a);
    }

    /* renamed from: t */
    public static boolean m22560t() {
        return f18574c[0].equals(m22535a().f18598a);
    }

    /* renamed from: u */
    public static boolean m22561u() {
        return f18578g[0].equals(m22535a().f18598a);
    }

    /* renamed from: v */
    private static C5526a m22562v() {
        C5526a aVar = new C5526a();
        String w = m22563w();
        String x = m22564x();
        String[] strArr = f18572a;
        if (m22537a(w, x, strArr)) {
            aVar.f18598a = strArr[0];
            String a = m22536a(f18592u);
            String[] split = a.split("_");
            if (split.length > 1) {
                aVar.f18599b = split[1];
            } else {
                aVar.f18599b = a;
            }
            return aVar;
        }
        String[] strArr2 = f18573b;
        if (m22537a(w, x, strArr2)) {
            aVar.f18598a = strArr2[0];
            aVar.f18599b = m22536a(f18593v);
            return aVar;
        }
        String[] strArr3 = f18574c;
        if (m22537a(w, x, strArr3)) {
            aVar.f18598a = strArr3[0];
            aVar.f18599b = m22536a(f18594w);
            return aVar;
        }
        String[] strArr4 = f18575d;
        if (m22537a(w, x, strArr4)) {
            aVar.f18598a = strArr4[0];
            aVar.f18599b = m22536a(f18595x);
            return aVar;
        }
        String[] strArr5 = f18576e;
        if (m22537a(w, x, strArr5)) {
            aVar.f18598a = strArr5[0];
            aVar.f18599b = m22536a(f18596y);
            return aVar;
        }
        String[] strArr6 = f18577f;
        if (m22537a(w, x, strArr6)) {
            aVar.f18598a = strArr6[0];
            aVar.f18599b = m22536a(f18597z);
            return aVar;
        }
        String[] strArr7 = f18578g;
        if (m22537a(w, x, strArr7)) {
            aVar.f18598a = strArr7[0];
            aVar.f18599b = m22536a(f18567A);
            return aVar;
        }
        String[] strArr8 = f18579h;
        if (m22537a(w, x, strArr8)) {
            aVar.f18598a = strArr8[0];
            aVar.f18599b = m22536a(f18568B);
            return aVar;
        }
        String[] strArr9 = f18580i;
        if (m22537a(w, x, strArr9)) {
            aVar.f18598a = strArr9[0];
            aVar.f18599b = m22536a(f18569C);
            return aVar;
        }
        String[] strArr10 = f18581j;
        if (m22537a(w, x, strArr10)) {
            aVar.f18598a = strArr10[0];
        } else {
            String[] strArr11 = f18582k;
            if (m22537a(w, x, strArr11)) {
                aVar.f18598a = strArr11[0];
            } else {
                String[] strArr12 = f18583l;
                if (m22537a(w, x, strArr12)) {
                    aVar.f18598a = strArr12[0];
                } else {
                    String[] strArr13 = f18584m;
                    if (m22537a(w, x, strArr13)) {
                        aVar.f18598a = strArr13[0];
                    } else {
                        String[] strArr14 = f18585n;
                        if (m22537a(w, x, strArr14)) {
                            aVar.f18598a = strArr14[0];
                        } else {
                            String[] strArr15 = f18586o;
                            if (m22537a(w, x, strArr15)) {
                                aVar.f18598a = strArr15[0];
                            } else {
                                String[] strArr16 = f18587p;
                                if (m22537a(w, x, strArr16)) {
                                    aVar.f18598a = strArr16[0];
                                } else {
                                    String[] strArr17 = f18588q;
                                    if (m22537a(w, x, strArr17)) {
                                        aVar.f18598a = strArr17[0];
                                    } else {
                                        String[] strArr18 = f18589r;
                                        if (m22537a(w, x, strArr18)) {
                                            aVar.f18598a = strArr18[0];
                                        } else {
                                            String[] strArr19 = f18590s;
                                            if (m22537a(w, x, strArr19)) {
                                                aVar.f18598a = strArr19[0];
                                            } else {
                                                String[] strArr20 = f18591t;
                                                if (m22537a(w, x, strArr20)) {
                                                    aVar.f18598a = strArr20[0];
                                                } else {
                                                    aVar.f18598a = x;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        aVar.f18599b = m22536a("");
        return aVar;
    }

    /* renamed from: w */
    private static String m22563w() {
        try {
            String str = Build.BRAND;
            return !TextUtils.isEmpty(str) ? str.toLowerCase() : "unknown";
        } catch (Throwable unused) {
            return "unknown";
        }
    }

    /* renamed from: x */
    private static String m22564x() {
        try {
            String str = Build.MANUFACTURER;
            return !TextUtils.isEmpty(str) ? str.toLowerCase() : "unknown";
        } catch (Throwable unused) {
            return "unknown";
        }
    }

    /* renamed from: b */
    private static String m22538b(String str) {
        String d = m22542d(str);
        if (!TextUtils.isEmpty(d)) {
            return d;
        }
        String e = m22544e(str);
        return (TextUtils.isEmpty(e) && Build.VERSION.SDK_INT < 28) ? m22540c(str) : e;
    }

    /* renamed from: c */
    private static String m22540c(String str) {
        try {
            Class<?> cls = Class.forName("android.os.SystemProperties");
            return (String) cls.getMethod("get", String.class, String.class).invoke(cls, str, "");
        } catch (Throwable unused) {
            return "";
        }
    }

    private static String m22542d(String str) {
        try {
            Runtime runtime = Runtime.getRuntime();
            StringBuilder sb = new StringBuilder();
            sb.append("getprop ");
            sb.append(str);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runtime.exec(sb.toString()).getInputStream()), 1024);
            String readLine = bufferedReader.readLine();
            bufferedReader.close();
            if (readLine != null) {
                return readLine;
            }
        } catch (Exception e) {
        }
        return "";
    }

    /* renamed from: e */
    private static String m22544e(String str) {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
            return properties.getProperty(str, "");
        } catch (Throwable unused) {
            return "";
        }
    }

    /* renamed from: a */
    private static boolean m22537a(String str, String str2, String... strArr) {
        for (String str3 : strArr) {
            if (str.contains(str3) || str2.contains(str3)) {
                return true;
            }
        }
        return false;
    }

    /* renamed from: a */
    private static String m22536a(String str) {
        String b = !TextUtils.isEmpty(str) ? m22538b(str) : "";
        if (TextUtils.isEmpty(b) || b.equals("unknown")) {
            try {
                String str2 = Build.DISPLAY;
                if (!TextUtils.isEmpty(str2)) {
                    b = str2.toLowerCase();
                }
            } catch (Throwable unused) {
            }
        }
        if (TextUtils.isEmpty(b)) {
            return "unknown";
        }
        return b;
    }
}