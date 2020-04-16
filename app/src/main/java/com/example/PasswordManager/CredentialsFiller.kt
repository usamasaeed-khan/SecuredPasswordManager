package com.example.PasswordManager

//import org.junit.experimental.results.ResultMatchers.isSuccessful

import android.app.assist.AssistStructure
import android.content.Context
import android.content.pm.PackageManager
import android.os.CancellationSignal
import android.service.autofill.*
import android.util.ArrayMap
import android.util.Log
import android.view.autofill.AutofillId
import android.view.autofill.AutofillValue
import android.widget.RemoteViews
import android.widget.Toast
import okhttp3.internal.http.StatusLine
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
//import sun.net.www.http.HttpClient
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URI
import java.security.MessageDigest
import java.util.zip.ZipFile


//import javax.xml.ws.Response


class CredentialsFiller: AutofillService() {
    override fun onFillRequest(p0: FillRequest, p1: CancellationSignal, p2: FillCallback) {

        val contexts:List<FillContext> = p0.fillContexts
        val structure = contexts[contexts.size-1].structure
        val foregroundAppsPackageName = structure.activityComponent.packageName
        val fields:Map<String,AutofillId> = getAutofillableFields(structure)
        Log.d("FIELDS", "Fields$fields")
        if(fields.isEmpty()){
            Toast.makeText(applicationContext,"No Autofill Hints Found.",Toast.LENGTH_LONG).show()
            p2.onSuccess(null)
        }


        val packageNameTemp = applicationContext.packageName




        //Toast.makeText(applicationContext,contexts[contexts.size-1].structure.activityComponent.packageName,Toast.LENGTH_LONG).show()


        val appsInfo = readManifest(applicationContext)


        for ((i,j) in appsInfo!!){

            println("$i\t$j")

        }

        val apk = ZipFile(appsInfo[foregroundAppsPackageName])

        val manifestData = apk.getEntry("AndroidManifest.xml")

        if(manifestData != null){


            val inputStream = apk.getInputStream(manifestData)

            val line:String

            val bufferedReader = BufferedReader(InputStreamReader(inputStream))

            line = bufferedReader.readText()

            var data = ""
            for(x in line){
                if(x.toByte() in 31..127)
                    data += x
            }
            println(data)


            if(data.contains("autoVerify")){



                var host = ""
                var hostStart = 0
                for(i in data.indices){
                    if(data[i] == 'w'){
                        var checkString = ""
                        for(j in 0 until 3){
                            checkString+=data[i+j]

                        }

                        if(checkString == "www"){
                            hostStart = i+3
                            break
                        }
                    }
                }
                if(hostStart != 0) {
                    for (i in hostStart+1 until data.length) {



                        if (data[i] == '.') {
                            var checkString = ""
                            for (j in 0 until 4) {
                                checkString += data[i + j]

                            }

                            if (checkString == ".com") {
                                break
                            }
                        }

                        host += data[i]


                    }
                    Toast.makeText(applicationContext,contexts[contexts.size-1].structure.activityComponent.packageName+" is Verified Application for www.$host.com",Toast.LENGTH_LONG).show()




                    val presentation = RemoteViews(packageNameTemp,R.layout.email_suggestion)

                    presentation.setTextViewText(R.id.email_suggestion_item,"www.$host.com")


                    val primaryEmailDataSet = Dataset.Builder(presentation)
                        .setValue(
                            fields["email"]!!,
                            AutofillValue.forText("abc@gmail.com")

                        ).build()



                    val secondaryDataSet = Dataset.Builder(presentation)
                        .setValue(
                            fields["password"]!!,
                            AutofillValue.forText("12345678")

                        ).build()

                    val response:FillResponse = FillResponse.Builder()
                        .addDataset(primaryEmailDataSet)
                        .addDataset(secondaryDataSet)
                        .build()

                    p2.onSuccess(response)




                }

                else {
                    Toast.makeText(applicationContext,contexts[contexts.size-1].structure.activityComponent.packageName+" is Verified Application.",Toast.LENGTH_LONG).show()
                }







            }

            else {

                Toast.makeText(applicationContext,contexts[contexts.size-1].structure.activityComponent.packageName+" is not a Verified Application.",Toast.LENGTH_LONG).show()

            }



        }

    }









//        val contexts = p0.fillContexts
//        val structure:AssistStructure = contexts[contexts.size-1].structure
//        val windowsNode:AssistStructure.WindowNode = structure.getWindowNodeAt(0)
//        val viewNode:AssistStructure.ViewNode = windowsNode.rootViewNode
//
//
//        val str:String = "Appear Please PLease Pleaseee"
//        val suggestion:RemoteViews = RemoteViews(packageName,R.layout.email_suggestion)
//        println("PACKAGE NAME: $packageName")
//        suggestion.setTextViewText(R.id.email_suggestion_item,str)
//
//
//        val suggestionDataset = Dataset.Builder(suggestion)
//            .setValue(viewNode.autofillId!!, AutofillValue.forText(str))
//            .build()
//
//        val response:FillResponse = FillResponse.Builder()
//            .addDataset(suggestionDataset)
//            .build()
//
//        p2.onSuccess(response)



//        println("\nContexts:\n\n\n\n")
//        for(i in contexts)
//            println(i.requestId)
//        println("\n\n\n\n")


        //var emailFields = ArrayList<AssistStructure.ViewNode>()
//        val structure = contexts[contexts.size-1].structure
//
//        println(emailFields)
////        val str:String ="This will appear in the autofill list for 'viewNode'."
////        val suggestion:RemoteViews  = RemoteViews(packageName, R.layout.email_suggestion)
////        suggestion.setTextViewText(R.id.email_suggestion_item, str)
//
//        emailFields=identifyEmailFields(structure.getWindowNodeAt(0).rootViewNode,emailFields)
//        if(emailFields.size==0)return
//
//        val remoteViewEmail = RemoteViews(packageName,R.layout.email_suggestion)
//        val remoteViewPassword = RemoteViews(packageName,R.layout.email_suggestion)
//
//        val sharedPreferences:SharedPreferences =
//            getSharedPreferences("EMAIL_STORAGE", Context.MODE_PRIVATE)
//
//        val email: String? = sharedPreferences.getString("email","")
//        val password: String? = sharedPreferences.getString("password","")
//
//        remoteViewEmail.setTextViewText(R.id.email_suggestion_item,email)
//        remoteViewPassword.setTextViewText(R.id.email_suggestion_item,password)
//
//        val emailField = emailFields[0]
//
//        val emailDataSet:Dataset =
//            Dataset.Builder(remoteViewEmail)
//                .setValue(
//                    emailField.autofillId!!,
//                    AutofillValue.forText(email)
//                ).build()
//
//        val passwordDataSet:Dataset =
//            Dataset.Builder(remoteViewPassword)
//                .setValue(
//                    emailField.autofillId!!,
//                    AutofillValue.forText(password)
//                ).build()
//
//        val response = FillResponse.Builder()
//            .addDataset(emailDataSet)
//            .addDataset(passwordDataSet)
//            .build()
//
//        p2.onSuccess(response)





    override fun onSaveRequest(p0: SaveRequest, p1: SaveCallback) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun identifyEmailFields(node:AssistStructure.ViewNode, emailFields:ArrayList<AssistStructure.ViewNode>):ArrayList<AssistStructure.ViewNode>{

        //if(node.className.contains("EditText")){
            println("\nID ENTRY:  ${node.idEntry}")
            val viewId:String = node.idEntry
            //if(viewId.contains("email") || viewId.contains("password") || viewId.contains("username")){
                emailFields.add(node)
                return emailFields
            //}
        //}
        //return ArrayList()

//        for(i in 0..node.childCount){
//            identifyEmailFields(node.getChildAt(i),emailFields)
//        }

    }

    private fun getAutofillableFields(structure: AssistStructure):Map<String,AutofillId>{
        val fields:MutableMap<String,AutofillId> = ArrayMap()
        val nodes = structure.windowNodeCount
        Log.d("NODES COUNT","$nodes")
        for(i in 0 until nodes){
            val node:AssistStructure.ViewNode = structure.getWindowNodeAt(i).rootViewNode
            addAutofillableFields(fields,node)
        }
        return fields
    }

    private fun addAutofillableFields(
        fields: MutableMap<String, AutofillId>,
        node: AssistStructure.ViewNode
    ) {
        val hints = node.autofillHints

        if(hints != null){
            val hint = hints[0].toLowerCase()
            val id: AutofillId? = node.autofillId
            if(!fields.containsKey(hint)){
                fields[hint] = id!!
            }
        }
        val childrenSize = node.childCount
        //Log.d("CHILDREN COUNT","$childrenSize")
        for(i in 0 until childrenSize){
            addAutofillableFields(fields,node.getChildAt(i))
        }
    }


//    private fun checkWebDomainAndBuildAutofillData(
//        packageName: String,
//        callback: SaveCallback
//    ) {
//        val webDomain: String
//        webDomain = try {
//            mClientViewMetadata.getWebDomain()
//        } catch (e: SecurityException) {
//            logw(e.message)
//            callback.onFailure(getString(R.string.security_exception))
//            return
//        }
//        if (webDomain != null && webDomain.length > 0) {
//            val req: DalCheckRequirement = mPreferences.getDalCheckRequirement()
//            mDalRepository.checkValid(req, DalInfo(webDomain, packageName),
//                object : DataCallback<DalCheck?>() {
//                    fun onLoaded(dalCheck: DalCheck) {
//                        if (dalCheck.linked) {
//                            logd("Domain %s is valid for %s", webDomain, packageName)
//                            buildAndSaveAutofillData()
//                        } else {
//                            loge(
//                                "Could not associate web domain %s with app %s",
//                                webDomain, packageName
//                            )
//                            callback.onFailure(getString(R.string.dal_exception))
//                        }
//                    }
//
//                    fun onDataNotAvailable(
//                        msg: String?,
//                        vararg params: Any?
//                    ) {
//                        logw(msg, params)
//                        callback.onFailure(getString(R.string.dal_exception))
//                    }
//                })
//        } else {
//            logd("no web domain")
//            buildAndSaveAutofillData()
//        }
//    }



    private fun readManifest(context:Context): HashMap<String, String>? {
        val pm:PackageManager = context.packageManager
        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA or PackageManager.GET_SHARED_LIBRARY_FILES)

        val appsInfo: HashMap<String, String>? = HashMap()





        for(i in apps){

            appsInfo?.set(i.packageName, i.publicSourceDir)

            println(i.publicSourceDir)
        }
        return appsInfo
    }

//    private fun getCertificateHash(context:FillContext):String{
//
//
//        //val packageName = context.packageName
//
//        val packageManager:PackageManager = applicationContext.packageManager
//
//        val info = packageManager.getPackageInfo(packageName,PackageManager.GET_SIGNATURES)
//
//        val hashes = ArrayList<String>(info.signatures.size)
//
//        for(sig in info.signatures){
//
//            val cert = sig.toByteArray()
//
//            val md = MessageDigest.getInstance("SHA-256")
//
//            md.update(cert)
//            var hex = ""
//            for(i in md.digest()){
//                hex+=String.format("%02X",i)
//            }
//
//            hashes.add(hex)
//        }
//
//        hashes.sort()
//
//        val hash = StringBuilder()
//
//        for(i in 0 until hashes.size){
//
//            hash.append(hashes[i])
//
//
//        }
//
//        return hash.toString()
//
//
//    }






}