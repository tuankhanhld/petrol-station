package com.example.tuank.petrol_station

import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class data{
    var db = FirebaseDatabase.getInstance().reference
    var auth = FirebaseAuth.getInstance()
    var s: String = ""

    //my information
    var myId: String = ""
    var myCurrentMoney: Int = 0
    var myName: String = ""
    var myPhoneNumber: Int = 0
    var myEmail: String = ""
    var myLocation: String = ""
    var myAge: Int = 0
    var myGender: String = ""
    var myRfidCode: Int = 0

    //information of change
    var numberStation: String = ""


    //getter and setter
    public fun get_myId():String{
        return this.myId
    }
    public fun get_myCurrentMoney():Int{
        return this.myCurrentMoney
    }
    public fun get_myName():String{
        return this.myName
    }
    public fun get_myPhoneNumber():Int{
        return this.myPhoneNumber
    }
    public fun get_myEmail():String{
        return this.myEmail
    }
    public fun get_myLocation():String{
        return this.myLocation
    }
    public fun get_myAge():Int{
        return this.myAge
    }
    public fun get_myGender():String{
        return this.myGender
    }
    public fun get_myRfidCode():Int{
        return this.myRfidCode
    }
    //setter
    public fun set_myId(myId:String){
        this.myId = myId
    }
    public fun set_myCurrentMoney(myCurrentMoney:Int){
        this.myCurrentMoney = myCurrentMoney
    }
    public fun set_myName(myName: String){
        this.myName = myName
    }
    public fun set_myPhoneNumber(myPhoneNumber: Int){
        this.myPhoneNumber = myPhoneNumber
    }
    public fun set_myEmail(myEmail: String){
        this.myEmail = myEmail
    }
    public fun set_myLocation(myLocation: String){
        this.myLocation = myLocation
    }
    public fun set_myAge(myAge: Int){
        this.myAge = myAge
    }
    public fun set_myGender(myGender: String){
        this.myGender = myGender
    }
    public fun set_myRfidCode(myRfidCode: Int){
        this.myRfidCode = myRfidCode
    }
    public fun set_stationId(stationId: String){
        this.numberStation = stationId
    }
    public fun get_stationId(): String{
        return this.numberStation
    }

    public fun subMoney(id: String, money: String){
        db.child("Account").child(id).child("money").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun onDataChange(p0: DataSnapshot) {
                val moneyCurent: String = p0.getValue().toString()
                val moneyNew: Int = moneyCurent.toInt() - money.toInt()
                db.child("Account").child(id).child("money").setValue(moneyNew)

            }
        })
    }

    public fun addMoney(id: String, money: String){
        db.child("Account").child(id).child("money").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun onDataChange(p0: DataSnapshot) {
                val moneyCurent: String = p0.getValue().toString()
                val moneyNew: Int = moneyCurent.toInt() + money.toInt()
                db.child("Account").child(id).child("money").setValue(moneyNew)
            }
        })
    }

    public fun addIncome(id: String, money: String, type: String){
        val cTime = SimpleDateFormat("yyyy/M/dd")
        val currentDateFull = cTime.format(Date())
        db.child(this@data.numberStation).child("income").child(currentDateFull).child(type).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun onDataChange(p0: DataSnapshot) {
                println("key: ${p0.key}")
                try {
                    val moneyCurent = p0.value.toString()
                    val moneyNew: Int = moneyCurent.toInt() + money.toInt()
                    db.child(this@data.numberStation).child("income").child(currentDateFull).child(type).setValue(moneyNew)
                }
                catch (e:Exception){
                    db.child(this@data.numberStation).child("income").child(currentDateFull).child(type).setValue(0)
                    addIncome(id, money, type)
                }

            }
        })
    }

    public fun addAmount(id: String, amount: String, type: String){
        val cTime = SimpleDateFormat("yyyy/M/dd")
        val currentDateFull = cTime.format(Date())

        db.child(this@data.numberStation).child("history_amount_sell").child(type).child(currentDateFull).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun onDataChange(p0: DataSnapshot) {
                try {
                    val amountCurrent: String = p0.getValue().toString()
                    val amountNew: Float = amountCurrent.toFloat() + amount.toFloat()
                    val roundOff = Math.round(amountNew * 100.0) / 100.0
                    db.child(this@data.numberStation).child("history_amount_sell").child(type).child(currentDateFull).setValue(roundOff)
                } catch (e:Exception){
                    db.child(this@data.numberStation).child("history_amount_sell").child(type).child(currentDateFull).setValue(0)
                    addAmount(id, amount, type)
                }

            }
        })
    }


    public fun get_station_current(id: String){
        set_myId(id)
        db.child("Account").child(id).child("pay_id").addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun onDataChange(p0: DataSnapshot) {
                val staitonId: String = p0.value.toString()
                set_stationId(staitonId)
            }
        })
    }
    public fun addNumberTrans(id: String, type: String){
        db.child("Account").child(id).child("transaction_infor").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun onDataChange(p0: DataSnapshot) {
                try {
                    val num = p0.child(type).value.toString().toInt()

                    db.child("Account").child(id).child("transaction_infor").child(type).setValue(num+ 1)
                }catch (e:Exception){
                    db.child("Account").child(id).child("transaction_infor").child(type).setValue(0)
                    addNumberTrans(id,type)
                }

            }
        })
    }


    public fun setvalue(id: String, value: String){
        db.child(id).setValue(value)
    }
    public fun setvalue(id: String, sub_id1: String, value: String){
        db.child(id).child(sub_id1).setValue(value)
    }
    public fun setvalue(id: String, sub_id1: String,sub_id2: String, value: String){
        db.child(id).child(sub_id1).child(sub_id2).setValue(value)
    }

}