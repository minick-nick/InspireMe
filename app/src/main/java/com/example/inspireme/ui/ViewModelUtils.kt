package com.example.inspireme.ui

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import retrofit2.HttpException
import java.io.IOException

object ViewModelUtils {
    fun updateQuotationDetails(
        quotations: List<QuotationDetails>,
        target: QuotationDetails,
        isSaved: Boolean
    ): List<QuotationDetails> {
        val quotationsMutableList = quotations.toMutableList()
            for (currentIndex in quotations.indices) {
                if (quotations[currentIndex].id == target.id) {
                    quotationsMutableList[currentIndex] = target.copy(isSaved = isSaved)
                    break
                }
            }
        return quotationsMutableList.toList()
    }

    suspend fun catchExceptions(block: suspend () -> QuotationsRequest): QuotationsRequest {
        return try {
            block()
        } catch (e: IOException) {
            QuotationsRequest.Error
        } catch (e: HttpException) {
            QuotationsRequest.Error
        }
    }

    fun shareQuotation(context: Context, quotation: QuotationDetails) {
        val shareIntent = Intent.createChooser(
            Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "${quotation.content}\n\n--${quotation.author}")
                type = "text/plain"
            }, null)

        startActivity(context, shareIntent, null)
    }
}