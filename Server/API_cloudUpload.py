from google.cloud import storage


def upload_blob(fileName,appName,comboName):
    """Uploads a file to the bucket."""
    bucket_name = "andmaldeploy-testphase1"
    source_file_name = '/home/debian/Documents/run_server/API_processing/Reports/' + comboName + '.pdf'
    destination_blob_name = 'reports/'+ comboName + '.pdf'

    storage_client = storage.Client.from_service_account_json('/home/debian/Documents/res/andmaldeploy-c91c9a33d965.json')
    bucket = storage_client.bucket(bucket_name)
    blob = bucket.blob(destination_blob_name)
    
    blob.upload_from_filename(source_file_name)

    print(
        "File {} uploaded to {}".format(
            source_file_name, destination_blob_name
        )
    )
    #NotificationGenerator(deviceToken,appName,fileName,appCombo)
