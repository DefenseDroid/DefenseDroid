## Install request module by running ->
#  pip3 install requests

# Replace the deviceToken key with the device Token for which you want to send push notification.
# Replace serverToken key with your serverKey from Firebase Console

# Run script by ->
# python3 fcm_python.py


import requests
import json

def NotificationGenerator(token, appName,fileName,comboName):
  serverToken = 'AAAAY9pa1Wo:APA91bGd_Fu8VZQh8JpBkTlDko_flKe2hiE5597Y9FjA533j8tplUszhw90qRiA9njvzhWJk4Cy1wtGNcc5a5dFRQ_quDeiu1rgWFNDQPe4jxlBGQq40iHqCVl1RuT2d0R2LnsCNnL6y'
  #deviceToken = token
  static = 'dd9_jXVPRd2roRcS_o1qZU:APA91bHHRzduFpvNuGQsZ9DTrFwesx7IUacVTOsOZ1j-cMLNAUn-879bHdgg-ePLaEONhL63pdIzUpS2QvTizgnLUEO0W6PzvQxc7YxUgtQt_lbktI-lYrsBJN8lEjYXdKQNA9_-fJI2'
  print(token)
  print(type(token))
  #static = 'dd9_jXVPRd2roRcS_o1qZUAPA91bHHRzduFpvNuGQsZ9DTrFwesx7IUacVTOsOZ1j-cMLNAUn-879bHdgg-ePLaEONhL63pdIzUpS2QvTizgnLUEO0W6PzvQxc7YxUgtQt_lbktI-lYrsBJN8lEjYXdKQNA9_-fJI2'
  print(token == static)
  headers = {
          'Content-Type': 'application/json',
          'Authorization': 'key=' + serverToken,
        }

  body = {
            'notification': {'title': 'Report for '+ appName + ' is ready',
                              'body': comboName
                              },
            'to':
                token,
            'priority': 'high',
          #   'data': dataPayLoad,
          }
  response = requests.post("https://fcm.googleapis.com/fcm/send",headers = headers, data=json.dumps(body))
  print(response.status_code)

  print(response.json())


#NotificationGenerator('dd9_jXVPRd2roRcS_o1qZUAPA91bHHRzduFpvNuGQsZ9DTrFwesx7IUacVTOsOZ1j-cMLNAUn-879bHdgg-ePLaEONhL63pdIzUpS2QvTizgnLUEO0W6PzvQxc7YxUgtQt_lbktI-lYrsBJN8lEjYXdKQNA9_-fJI2')