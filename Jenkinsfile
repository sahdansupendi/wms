pipeline {
    agent any

    // ======================================================
    // ENVIRONMENT VARIABLES
    // Sesuaikan nilai-nilai di bawah ini dengan environment-mu
    // ======================================================
    environment {
        // Nama aplikasi
        APP_NAME        = 'wms'
        // Versi artifact (sesuai pom.xml)
        APP_VERSION     = '0.0.1-SNAPSHOT'
        // Nama file WAR hasil build
        WAR_FILE        = "${APP_NAME}-${APP_VERSION}.war"

        // Direktori deploy di server tujuan (mis. Tomcat webapps)
        DEPLOY_DIR      = '/opt/tomcat/webapps'
        // User SSH untuk koneksi ke server tujuan
        DEPLOY_USER     = 'ubuntu'
        // Host server tujuan (ganti dengan IP/domain server)
        DEPLOY_HOST     = '192.168.1.100'

        // Credential ID yang sudah didaftarkan di Jenkins
        // (SSH key untuk deploy ke server)
        SSH_CRED_ID     = 'ssh-deploy-key'

        // Lokasi tools (sesuaikan dengan path di Jenkins agent)
        JAVA_HOME       = tool name: 'JDK-21', type: 'jdk'
        MAVEN_HOME      = tool name: 'Maven-3', type: 'maven'
        PATH            = "${JAVA_HOME}/bin:${MAVEN_HOME}/bin:${PATH}"
    }

    // ======================================================
    // OPSI PIPELINE
    // ======================================================
    options {
        // Simpan max 10 history build di Jenkins UI
        buildDiscarder(logRotator(numToKeepStr: '10'))
        // Timeout keseluruhan pipeline: 30 menit
        timeout(time: 30, unit: 'MINUTES')
        // Tidak mengizinkan build berjalan bersamaan di branch yang sama
        disableConcurrentBuilds()
    }

    // ======================================================
    // TRIGGER OTOMATIS
    // Pipeline ini otomatis jalan ketika:
    // - Ada push ke branch main atau develop (via GitHub Webhook)
    // ======================================================
    triggers {
        // Polling SCM setiap 5 menit sebagai fallback jika webhook tidak tersedia
        // Hapus ini jika sudah pakai GitHub Webhook
        pollSCM('H/5 * * * *')
    }

    // ======================================================
    // STAGE-STAGE CI/CD
    // ======================================================
    stages {

        // --------------------------------------------------
        // STAGE 1: CHECKOUT
        // Mengambil kode terbaru dari repository Git
        // --------------------------------------------------
        stage('Checkout') {
            steps {
                echo '=== Mengambil kode dari repository ==='
                checkout scm
                // Tampilkan info commit terakhir
                sh 'git log -1 --pretty=format:"Commit: %H%nAuthor: %an%nDate: %ad%nMessage: %s"'
            }
            post {
                failure {
                    echo 'Checkout GAGAL! Periksa konfigurasi repository Git di Jenkins.'
                }
            }
        }

        // --------------------------------------------------
        // STAGE 2: BUILD & COMPILE
        // Mengompilasi source code Java
        // --------------------------------------------------
        stage('Build & Compile') {
            steps {
                echo '=== Kompilasi source code ==='
                sh 'mvn clean compile -B -Dmaven.test.skip=true'
            }
            post {
                failure {
                    echo 'Kompilasi GAGAL! Periksa error di atas.'
                }
            }
        }

        // --------------------------------------------------
        // STAGE 3: UNIT TEST
        // Menjalankan semua unit test
        // --------------------------------------------------
        stage('Unit Test') {
            steps {
                echo '=== Menjalankan Unit Test ==='
                sh 'mvn test -B'
            }
            post {
                always {
                    // Tampilkan laporan JUnit test di Jenkins UI
                    junit allowEmptyResults: true,
                          testResults: 'target/surefire-reports/*.xml'
                }
                failure {
                    echo 'Unit Test GAGAL! Lihat laporan test di atas.'
                }
            }
        }

        // --------------------------------------------------
        // STAGE 4: CODE QUALITY CHECK (SonarQube)
        // Opsional: Hapus stage ini jika tidak pakai SonarQube
        // --------------------------------------------------
        stage('Code Quality (SonarQube)') {
            // Hanya jalankan di branch main atau develop
            when {
                anyOf {
                    branch 'main'
                    branch 'develop'
                }
            }
            steps {
                echo '=== Analisis kualitas kode dengan SonarQube ==='
                // 'SonarQube' adalah nama SonarQube server yang didaftarkan di Jenkins
                withSonarQubeEnv('SonarQube') {
                    sh '''
                        mvn sonar:sonar \
                            -Dsonar.projectKey=wms \
                            -Dsonar.projectName="Warehouse Management System" \
                            -Dsonar.java.source=21 \
                            -B
                    '''
                }
            }
        }

        // --------------------------------------------------
        // STAGE 5: PACKAGE
        // Membungkus aplikasi menjadi file WAR
        // --------------------------------------------------
        stage('Package (WAR)') {
            steps {
                echo '=== Membuat file WAR ==='
                sh 'mvn package -B -DskipTests'
                // Verifikasi file WAR berhasil dibuat
                sh "ls -lh target/*.war"
            }
            post {
                success {
                    // Simpan WAR sebagai artifact di Jenkins
                    archiveArtifacts artifacts: 'target/*.war',
                                     fingerprint: true,
                                     allowEmptyArchive: false
                    echo "WAR berhasil dibuat: target/${WAR_FILE}"
                }
            }
        }

        // --------------------------------------------------
        // STAGE 6: DEPLOY KE DOCKER
        // Build image Docker & jalankan container aplikasi WMS
        // --------------------------------------------------
        stage('Deploy ke Docker') {
            steps {
                echo '=== Deploying WMS ke Docker Container ==='
                sh '''
                    # Stop & hapus container lama jika ada
                    docker stop wms-app || true
                    docker rm wms-app || true

                    # Build Docker image dari Dockerfile
                    docker build -t wms-app:latest .

                    # Jalankan container di port 8081 dengan koneksi database ke host.docker.internal
                    docker run -d \
                      -p 8081:8080 \
                      --add-host=host.docker.internal:host-gateway \
                      -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/db_wms \
                      --name wms-app wms-app:latest
                '''
                echo '=== Container WMS berhasil berjalan di http://localhost:8081 ==='
            }
            post {
                failure {
                    echo 'Deploy Docker GAGAL! Periksa daemon Docker.'
                }
            }
        }

        // --------------------------------------------------
        // STAGE 7: HEALTH CHECK
        // Verifikasi aplikasi berjalan di Docker
        // --------------------------------------------------
        stage('Health Check') {
            steps {
                echo '=== Verifikasi aplikasi WMS berjalan ==='
                sleep(time: 10, unit: 'SECONDS')
                sh 'curl -I http://localhost:8081 || echo "Aplikasi dapat diakses di http://localhost:8081"'
            }
        }
    }

    // ======================================================
    // POST BUILD: Aksi setelah pipeline selesai
    // ======================================================
    post {
        success {
            echo "BUILD & DEPLOY SUKSES! Branch: ${env.BRANCH_NAME}, Build: #${env.BUILD_NUMBER}"
        }
        failure {
            echo "PIPELINE GAGAL! Branch: ${env.BRANCH_NAME}, Build: #${env.BUILD_NUMBER}"
        }
        always {
            // Bersihkan workspace setelah build (pakai deleteDir yang built-in)
            deleteDir()
        }
    }
}
