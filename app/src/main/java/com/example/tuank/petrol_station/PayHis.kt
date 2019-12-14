package com.example.tuank.petrol_station

data class PayHis(var typeHis: String, var amountHis: String, var moneyHis: String, var dateHis: String) {
}

data class RechargeHis(var seriesNum: String, var emailHis: String, var moneyHis: String, var dateHis: String){

}

data class TransferHis(var from: String, var to: String, var moneyHis: String, var dateHis: String){

}