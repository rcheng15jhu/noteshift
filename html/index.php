<!--
File handling and transfer for
NoteShift 1.0
-->


<?php
//if (isset($_POST['submit'])){
    print_r($_FILES);
    $target_dir = "uploads/";
    print_r($_COOKIE);
    $target_file = $target_dir .  $_COOKIE["source"] . "$" . $_COOKIE["destination"] . "$" . time() . "_" . basename($_FILES["submittedFile"]["name"]);
    print_r($target_file);
    print_r($_FILES["submittedFile"]["tmp_name"]);
    $uploadOk = 1;
    $imageFileType = strtolower(pathinfo($target_file,PATHINFO_EXTENSION));
    
//    if (isset($_FILES['submittedFile'])) {
//    $fileName = $_FILES['submittedFile']['name'];
//    $fileTmpName = $_FILES['submittedFile']['tmp_name'];
//    $fileSize = $_FILES['submittedFile']['size'];
//    $fileError = $_FILES['submittedFile']['error'];
//    $fileType = $_FILES['submittedFile']['type'];

//    $fileExt = strtolower(end(explode('.', $fileName)));
        
    $allowed = array('pdf', 'jpeg', 'jpg');

        // Allow certain file formats
    if (in_array($imageFileType, $allowed) === false){
        echo "Sorry, only JPG, JPEG, & PDF files are allowed.";
        $uploadOk = 0;
    }
        // Check if $uploadOk is set to 0 by an error
        if ($uploadOk === 0) {
            echo "Sorry, your file was not uploaded.";
            // if everything is ok, try to upload file
        } else {
            if (move_uploaded_file($_FILES["submittedFile"]["tmp_name"], $target_file)) {
                echo "The file ". basename( $_FILES["submittedFile"]["name"]). " has been uploaded.";
                header('Location: loading.html');

            } else {
                echo "\n\nSorry, there was an error uploading your file.";
            }
        }
//        if (empty($fileError)===true) {
//            move_uploaded_file($fileTmpName, "uploads/".$fileName)
//            //$fileNameNew = uniqid('', true).".".$fileExt;
//            //$fileDestination = 'uploads/'.$fileNameNew;
//            //move_uploaded_file($fileTmpName, $fileDestination);
//            header("Location: learning.html?uploadsuccess");
//            echo "Success";
//        }
//        else{
//            echo "There was an error uploading your file!";
//        }
    
//}
?>
