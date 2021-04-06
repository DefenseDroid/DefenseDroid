import os
from pyaxmlparser import APK
from keras.models import load_model
import numpy as np
from google.cloud import storage
import mysql.connector
import time
from pdfmaker import createPDF
import requests
import json
from Notifier import NotificationGenerator
from cloudUpload import upload_blob



def procedure(fileName,appName,deviceToken):
  client = storage.Client()
  with open('/home/debian/Documents/run_server/testapk.apk','wb') as f:
      client.download_blob_to_file('gs://andmaldeploy-testphase1/apks/'+ fileName + '.apk',f)

  appUndertest = "/home/debian/Documents/run_server/testapk.apk"
  print(appUndertest)
  def processPermission(arr1):
    res = []
    for i in arr1:
      temp = i.split('.')
      if 'permission' in temp:
        ind = temp.index('permission')
        res.append(".".join(temp[ind:]))
    return res

  predictionModel = load_model('/home/debian/Documents/res/Permissions/PermissionModel.hdf5')  

  setPerms = []
  f1 = open('/home/debian/Documents/res/Permissions/PermissionListForPrediction.txt')
  temp = f1.read()
  setPerms = temp[2:-2].split("', '")
  #print(setPerms)
  a = APK(appUndertest)
  perms = processPermission(a.get_permissions())
  predictionList = [0] * len(setPerms)
  for i in perms:
      if i in setPerms:
          predictionList[setPerms.index(i)] = 1
  with open('permissionToPDF.txt', 'w') as ppdf:
    ppdf.writelines([perm+"\n" for perm in perms])
  permissionPrediction = predictionModel.predict(np.array(predictionList, ndmin=3))
  print(predictionModel.predict(np.array(predictionList, ndmin=3)))

  def recProcess(arr1):
    res = []
    for i in arr1:
      temp = i.split('.')
      if 'service' in temp:
        ind = temp.index('service')
        res.append(".".join(temp[ind:]))
      elif 'intent' in temp:
        ind = temp.index('intent')
        res.append(".".join(temp[ind:]))
      else:
        res.append(i.split(".")[-1])
    return res
  predictionModel = load_model('/home/debian/Documents/res/Receivers/Receivers_Model_LSTM.hdf5')

  setRecievers = []
  f1 = open('/home/debian/Documents/res/Receivers/ReceiverListForPrediction.txt')
  temp = f1.read()
  setRecievers = temp[2:-2].split("', '")
  #setRecievers

  a = APK(appUndertest)
  recievers = recProcess(a.get_receivers())
  #print(recievers)

  predictionList = [0] * len(setRecievers)
  for i in recievers:
      if i in setRecievers:
          predictionList[setRecievers.index(i)] = 1

  pred = np.array(predictionList, ndmin=2)
  #pred.shape
  recieversPrediction = predictionModel.predict(np.array(predictionList, ndmin=3))
  print(predictionModel.predict(np.array(predictionList, ndmin=3)))

  def serviceProcess(arr1):
    res = []
    for i in arr1:
      temp = i.split('.')
      if 'service' in temp:
        ind = temp.index('service')
        res.append(".".join(temp[ind:]))
      elif 'intent' in temp:
        ind = temp.index('intent')
        res.append(".".join(temp[ind:]))
      else:
        res.append(i.split(".")[-1])
    return res

  predictionModel = load_model('/home/debian/Documents/res/Services/Services_Model_LSTM.hdf5')

  setServices = []
  f1 = open('/home/debian/Documents/res/Services/ServiceListForPrediction.txt')
  temp = f1.read()
  setServices = temp[2:-2].split("', '")
  #setServices

  a = APK(appUndertest)
  services = serviceProcess(a.get_services())

  predictionList = [0] * len(setServices)
  for i in services:
      if i in setServices:
          predictionList[setServices.index(i)] = 1

  pred = np.array(predictionList, ndmin=2)
  servicesPrediction = predictionModel.predict(np.array(predictionList, ndmin=3))
  print(predictionModel.predict(np.array(predictionList, ndmin=3)))
  threshold = 0.51
  appName2 = appName.replace(' ', '_')
  comboName = appName2 + '@' + fileName
  createPDF(fileName,permissionPrediction,recieversPrediction,servicesPrediction,appName,threshold,comboName)
  upload_blob(fileName,appName,comboName)
  NotificationGenerator(deviceToken,appName,fileName,comboName)


sqlDelete = "DELETE FROM FILESTORE WHERE filename = %s"
while True:
  mydb = mysql.connector.connect(
        host="localhost",
        user="root",
        password="toor",
        database="ANDMAL"
        )
  mycursor = mydb.cursor()
  mycursor.execute("SELECT * FROM FILESTORE")
  rs = mycursor.fetchall()
  row_count = mycursor.rowcount
  if row_count > 0:
    for i in range(row_count):
      procedure(rs[i][1],rs[i][4],rs[i][3])
      mycursor.execute(sqlDelete, (rs[i][1],))
      mydb.commit()

