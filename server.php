<?php
define('db_user', 'hazim');
define('db_password', 'password');
define('db_host', 'localhost');
define('db_name', 'resume');

$conn = mysqli_connect(db_host, db_user, db_password, db_name);
if (mysqli_connect_errno()) {
    echo "Failed to connect to MySQL" . mysqli_connect_error();
}

// Check if the required parameters are present
if (isset($_POST['carPlate']) && isset($_POST['hours'])) {
    $carPlate = $_POST['carPlate'];
    $hours = $_POST['hours'];
    $price = $hours * 5;

    // Insert data into the database
    $query = "INSERT INTO parking_app (car_plate, hours,price) VALUES ('$carPlate', '$hours', '$price')";
    if (mysqli_query($conn, $query)) {
        echo "Data inserted successfully";
    } else {
        echo "Error inserting data: " . mysqli_error($conn);
    }

    // Close the database connection
    mysqli_close($conn);
} else {
    echo "$carPlate and"."$hours";
}
?>
