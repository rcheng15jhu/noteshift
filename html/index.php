<?php
//if (isset($_POST['submit'])){
    if (isset($_FILES['submittedFile'])) {
    $fileName = $_FILES['submittedFile']['name'];
    $fileTmpName = $_FILES['submittedFile']['tmp_name'];
    $fileSize = $_FILES['submittedFile']['size'];
    $fileError = $_FILES['submittedFile']['error'];
    $fileType = $_FILES['submittedFile']['type'];

    $fileExt = strtolower(end(explode('.', $fileName)));

    $allowed = array('pdf');

    if (in_array($fileExt, $allowed) === false){
        $fileError[]="Extension not allowed, please choose a PDF file.";
        print_r($fileError);
    }
        
        if (empty($fileError)===true) {
            move_uploaded_file($fileTmpName, "uploads/".$fileName)
            //$fileNameNew = uniqid('', true).".".$fileExt;
            //$fileDestination = 'uploads/'.$fileNameNew;
            //move_uploaded_file($fileTmpName, $fileDestination);
            header("Location: learning.html?uploadsuccess");
        }
        else{
            echo "There was an error uploading your file!";
        }
    
}
?>
