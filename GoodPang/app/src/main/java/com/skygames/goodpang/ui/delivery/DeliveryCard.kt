package com.skygames.goodpang.ui.delivery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.skygames.goodpang.R
import com.skygames.goodpang.data.model.Delivery
import com.skygames.goodpang.data.network.NetworkModule
import com.skygames.goodpang.ui.theme.DeliveryBlueBadgeBackground
import com.skygames.goodpang.ui.theme.DeliveryStatusColor
import com.skygames.goodpang.ui.theme.DeliveryTextSecondary

@Composable
fun DeliveryCard(
    delivery: Delivery,
    isCompleting: Boolean,
    onCompleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ShippingBadge()
                Text(
                    text = stringResource(R.string.delivery_start_date, formatDeliveryStartDate(delivery.deliveryStartDate)),
                    style = MaterialTheme.typography.bodySmall,
                    color = DeliveryTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = NetworkModule.resolveImageUrl(delivery.productImageUrl),
                    contentDescription = delivery.productName,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = delivery.productName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = delivery.storeName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = DeliveryTextSecondary
                    )
                    Text(
                        text = stringResource(R.string.delivery_item_count, delivery.itemCount),
                        style = MaterialTheme.typography.bodyMedium,
                        color = DeliveryTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            InfoRow(
                stringResource(R.string.delivery_label_delivery_no), delivery.deliveryNo.toString(),
                stringResource(R.string.delivery_label_order_no), delivery.orderNo.toString()
            )
            InfoRow(
                stringResource(R.string.delivery_label_service_code), delivery.deliveryServiceCode,
                stringResource(R.string.delivery_label_invoice_no), delivery.invoiceNo
            )
            InfoRow(
                stringResource(R.string.delivery_label_buyer_name), delivery.buyerName,
                stringResource(R.string.delivery_label_buyer_phone), delivery.buyerPhone
            )
            InfoRow(
                stringResource(R.string.delivery_label_address),
                "(${delivery.zipcode}) ${delivery.address} ${delivery.detailAddress}"
            )
            InfoRow(
                stringResource(R.string.delivery_label_status), delivery.deliveryStatus,
                valueColor = DeliveryStatusColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onCompleteClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = !isCompleting,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                if (isCompleting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(stringResource(R.string.delivery_complete_button))
                }
            }
        }
    }
}

@Composable
private fun ShippingBadge() {
    Row(
        modifier = Modifier
            .background(DeliveryBlueBadgeBackground, RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.LocalShipping,
            contentDescription = null,
            tint = DeliveryStatusColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = stringResource(R.string.delivery_status_shipping),
            color = DeliveryStatusColor,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun InfoRow(
    label1: String,
    value1: String,
    label2: String? = null,
    value2: String? = null,
    valueColor: Color = Color.Unspecified
) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        InfoField(label1, value1, valueColor, Modifier.weight(1f))
        if (label2 != null && value2 != null) {
            InfoField(label2, value2, Color.Unspecified, Modifier.weight(1f))
        }
    }
}

@Composable
private fun RowScope.InfoField(label: String, value: String, valueColor: Color, modifier: Modifier) {
    Row(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = DeliveryTextSecondary,
            modifier = Modifier.width(64.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = if (valueColor == Color.Unspecified) MaterialTheme.colorScheme.onSurface else valueColor,
            fontWeight = if (valueColor == Color.Unspecified) FontWeight.Normal else FontWeight.SemiBold
        )
    }
}
