package com.mozhimen.maintaink.net.host

import com.mozhimen.kotlin.elemk.commons.I_AListener
import com.mozhimen.kotlin.utilk.android.util.UtilKLogWrapper
import com.mozhimen.kotlin.utilk.commons.IUtilK
import com.mozhimen.netk.okhttp3.interceptor.bases.BaseInterceptorHostSelection
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * @ClassName MaintainKNetHostInterceptor
 * @Description TODO
 * @Author mozhimen
 * @Date 2024/12/30
 * @Version 1.0
 */
class MaintainKNetHostInterceptor(
    private val _getDrNetEnable: I_AListener<Boolean>,
    private val _getDrNetUrl: I_AListener<String>,
) : BaseInterceptorHostSelection(), IUtilK {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request: Request = chain.request()

        //降级容灾
        val drNetEnable = _getDrNetEnable.invoke()
        val drNetUrl = _getDrNetUrl.invoke()
        if (drNetEnable && drNetUrl.isNotEmpty()) {
            if (_httpUrl == null) {
                _httpUrl = drNetUrl.toHttpUrlOrNull()
            }
            if (_httpUrl != null) {
                try {
                    val newUrl: HttpUrl = request.url.newBuilder()
                        .scheme(_httpUrl!!.scheme)
                        .host(_httpUrl!!.toUrl().toURI().host)
                        .port(_httpUrl!!.port)
                        .build()
                    UtilKLogWrapper.d(TAG, "intercept: newUrl $newUrl")
                    request = request.newBuilder()
                        .url(newUrl)
                        .build()
                } catch (_: Exception) {
                }
            }
        }
        return chain.proceed(request)
    }
}