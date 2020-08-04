//Created by Matthew Reaney, cylinder physics updated by Mark Megarry June-July 2020			
			//variable declaration
			var checkElms = new JSCircuitElm(0);
			while(checkElms.getElmListSize() == 0){
				console.log("ERROR: ELMLIST EMPTY");
			}	//Wait for JSCircuitElm to load
            let physicsWorld, scene, camera, renderer, rigidBodies = [], tmpTrans = null
            let Cylinder = null // global declaration of moving part
            const STATE = { DISABLE_DEACTIVATION : 4 } // enables dynamic motion
            let boxBody = null, cylinderBody = null, boxPivot = null, cylinderPivot = null // global declaration of joint data
            //Ammojs Initialization
            let moveDirection = { up: 0, down: 0 } // used to store button presses
            let camDirection = { x: 0, y:0, z:0 }, camPos = { x:0, y:30, z:70 } // used for camera control
            //Motor sim values
            var inductor = new JSCircuitElm(2);
            var resistor = new JSCircuitElm(1);
            var source = new JSCircuitElm(0);
            var backEMF = new JSCircuitElm(6);
            let thetaDot = 0, K = 0.1, i = resistor.getCurrent(), v = source.getVoltageDiff(), L = 1, iDot = 0, R = 0.5, J = 0.01, b=0.1;
            
            Ammo().then( start )
            
            function start() // main loop used to call functions
            {
                  //code goes here
                  tmpTrans = new Ammo.btTransform();
                  setupPhysicsWorld();
                  setupGraphics();
                  createBlock();
                  createBox();
                  createCylinder();
                  setupEventHandlers();
                  renderFrame();
            }
        
            function setupPhysicsWorld() // sets up physics world
            {
                    let collisionConfiguration  = new Ammo.btDefaultCollisionConfiguration(),
                        dispatcher = new Ammo.btCollisionDispatcher(collisionConfiguration),
                        overlappingPairCache = new Ammo.btDbvtBroadphase(),
                        solver = new Ammo.btSequentialImpulseConstraintSolver();
            
                    physicsWorld = new Ammo.btDiscreteDynamicsWorld(dispatcher, overlappingPairCache, solver, collisionConfiguration);
                    physicsWorld.setGravity(new Ammo.btVector3(0, -10, 0));
            }
            
            function setupGraphics() // sets up the visuals
            {
                //create clock for timing
                clock = new THREE.Clock();

                //create the scene
                scene = new THREE.Scene();
                scene.background = new THREE.Color( 0xbfd1e5 );

                //create camera
                camera = new THREE.PerspectiveCamera( 60, window.innerWidth / window.innerHeight, 0.2, 5000 );
                camera.position.set( 0, 30, 70 );
                camera.lookAt(new THREE.Vector3(0, 0, 0));

                //Add hemisphere light
                let hemiLight = new THREE.HemisphereLight( 0xffffff, 0xffffff, 0.1 );
                hemiLight.color.setHSL( 0.6, 0.6, 0.6 );
                hemiLight.groundColor.setHSL( 0.1, 1, 0.4 );
                hemiLight.position.set( 0, 50, 0 );
                scene.add( hemiLight );

                //Add directional light
                let dirLight = new THREE.DirectionalLight( 0xffffff , 1);
                dirLight.color.setHSL( 0.1, 1, 0.95 );
                dirLight.position.set( -1, 1.75, 1 );
                dirLight.position.multiplyScalar( 100 );
                scene.add( dirLight );

                dirLight.castShadow = true;

                dirLight.shadow.mapSize.width = 2048;
                dirLight.shadow.mapSize.height = 2048;

                let d = 50;

                dirLight.shadow.camera.left = -d;
                dirLight.shadow.camera.right = d;
                dirLight.shadow.camera.top = d;
                dirLight.shadow.camera.bottom = -d;
                dirLight.shadow.camera.far = 13500;

              //Setup the renderer WITH DEBUG
                var container = document.getElementById( 'scene3d' );	//DEBUG
                document.body.appendChild( container );					//DEBUG
                renderer = new THREE.WebGLRenderer( { antialias: true } );
                renderer.setClearColor( 0xbfd1e5 );
                renderer.setPixelRatio( window.devicePixelRatio );
                renderer.setSize( container.offsetWidth, container.offsetHeight );
                container.appendChild( renderer.domElement );			//DEBUG
               //document.body.appendChild( renderer.domElement );
                                       
                renderer.gammaInput = true;
                renderer.gammaOutput = true;
                renderer.shadowMap.enabled = true;
            }
                                                     
            function renderFrame() // refreshes visuals
            {
                let deltaTime = clock.getDelta();
                moveCylinder( deltaTime );
                updatePhysics( deltaTime );
                renderer.render( scene, camera );
                requestAnimationFrame( renderFrame );
            }
            
            function setupEventHandlers() // calls keyboard input checks
            {
                window.addEventListener( 'keydown', handleKeyDown, false);
                window.addEventListener( 'keyup', handleKeyUp, false);
            }

            function handleKeyDown(event) // checks if button is pressed
            {
                let keyCode = event.keyCode;
                switch(keyCode)
                {
                    // motor rotation
                    case 16: // left shift
                    moveDirection.up = 1
                       break;
                       
                    case 17: // left control
                    moveDirection.down = 1
                       break;
                       
                    // camera control code
                    case 13: // enter, resets camera
                    camDirection.x = 0; camDirection.y = 0; camDirection.z = 0;
                    camPos.x = 0; camPos.y = 30, camPos.z = 70;
                    camera.position.set(camPos.x, camPos.y, camPos.z);
                    camera.lookAt(new THREE.Vector3( camDirection.x, camDirection.y, camDirection.z));
                        break;
                        
                    case 37: // left arrow, moves camera to left
                    camDirection.x -= 5;
                    camPos.x -= 5;
                    camera.position.set( camPos.x, camPos.y, camPos.z );
                    camera.lookAt(new THREE.Vector3( camDirection.x, camDirection.y, camDirection.z));
                       break;
                       
                    case 38: // up arrow, moves camera up
                    camDirection.y -= 5;
                    camPos.y -= 5;
                    camera.position.set( camPos.x, camPos.y, camPos.z );
                    camera.lookAt(new THREE.Vector3( camDirection.x, camDirection.y, camDirection.z));
                       break;
                       
                    case 39: // right arrow, moves camera right
                    camDirection.x += 5;
                    camPos.x += 5;
                    camera.position.set( camPos.x, camPos.y, camPos.z );
                    camera.lookAt(new THREE.Vector3( camDirection.x, camDirection.y, camDirection.z));
                       break;
                       
                    case 40: // down arrow, moves camera down
                    camDirection.y += 5;
                    camPos.y += 5;
                    camera.position.set( camPos.x, camPos.y, camPos.z );
                    camera.lookAt(new THREE.Vector3( camDirection.x, camDirection.y, camDirection.z));
                       break;
                       
                    case 65: // a, rotates camera left
                    camDirection.x -= 5;
                    camera.position.set( camPos.x, camPos.y, camPos.z );
                    camera.lookAt(new THREE.Vector3( camDirection.x, camDirection.y, camDirection.z));
                       break;
                       
                    case 68: // d, rotates camera right
                    camDirection.x += 5;
                    camera.position.set( camPos.x, camPos.y, camPos.z );
                    camera.lookAt(new THREE.Vector3( camDirection.x, camDirection.y, camDirection.z));
                       break;

                    case 83: // s, rotates camera down
                    camDirection.y -= 5;
                    camera.position.set( camPos.x, camPos.y, camPos.z );
                    camera.lookAt(new THREE.Vector3( camDirection.x, camDirection.y, camDirection.z));
                       break;
                    case 87: // w, rotates camera up
                    camDirection.y += 5;
                    camera.position.set( camPos.x, camPos.y, camPos.z );
                    camera.lookAt(new THREE.Vector3( camDirection.x, camDirection.y, camDirection.z));
                       break;
                       
                    case 187: // + \ =, moves camera away
                    camDirection.z +=  5;
                    camPos.z +=1;
                    camera.position.set( camPos.x, camPos.y, camPos.z );
                    camera.lookAt(new THREE.Vector3( camDirection.x, camDirection.y, camDirection.z));
                       break;
                       
                    case 189: // - \ _, moves camera closer
                    camDirection.z -= 5;
                    camPos.z -=1;
                    camera.position.set( camPos.x, camPos.y, camPos.z );
                    camera.lookAt(new THREE.Vector3( camDirection.x, camDirection.y, camDirection.z));
                       break;
                }
            }

            function handleKeyUp(event) // checks if button is released
            {
                let keyCode = event.keyCode;

                switch(keyCode)
                {
                    // motor roation
                    case 16: // left shift
                    moveDirection.up = 0
                       break;
                       
                    case 17: // left control
                    moveDirection.down = 0
                       break;
                }
            }
                                                     
            function createBlock() // creates the floor
            {
                let pos = {x: 0, y: 0, z: 0};
                let scale = {x: 50, y: 2, z: 50};
                let quat = {x: 0, y: 0, z: 0, w: 1};
                let mass = 0;

                //threeJS Section
                let blockPlane = new THREE.Mesh(new THREE.BoxBufferGeometry(), new THREE.MeshPhongMaterial({color: 0xa0afa4}));

                blockPlane.position.set(pos.x, pos.y, pos.z);
                blockPlane.scale.set(scale.x, scale.y, scale.z);

                blockPlane.castShadow = true;
                blockPlane.receiveShadow = true;

                scene.add(blockPlane);


                //Ammojs Section
                let transform = new Ammo.btTransform();
                transform.setIdentity();
                transform.setOrigin( new Ammo.btVector3( pos.x, pos.y, pos.z ) );
                transform.setRotation( new Ammo.btQuaternion( quat.x, quat.y, quat.z, quat.w ) );
                let motionState = new Ammo.btDefaultMotionState( transform );

                let colShape = new Ammo.btBoxShape( new Ammo.btVector3( scale.x * 0.5, scale.y * 0.5, scale.z * 0.5 ) );
                colShape.setMargin( 0.05 );

                let localInertia = new Ammo.btVector3( 0, 0, 0 );
                colShape.calculateLocalInertia( mass, localInertia );

                let rbInfo = new Ammo.btRigidBodyConstructionInfo( mass, motionState, colShape, localInertia );
                let body = new Ammo.btRigidBody( rbInfo );

                physicsWorld.addRigidBody( body );
            }
                                
            function createBox() // creates motor box
            {
                let pos = {x: 0, y: 3, z: 0};
                let scale = {x: 5, y: 5, z: 5};
                let quat = {x: 0, y: 0, z: 0, w: 1};
                let mass = 0;

                //threeJS Section
                let box = new THREE.Mesh(new THREE.BoxBufferGeometry(), new THREE.MeshPhongMaterial({color: 0x9ca3ad}));

                box.position.set(pos.x, pos.y, pos.z);
                box.scale.set(scale.x, scale.y, scale.z);

                box.castShadow = true;
                box.receiveShadow = true;

                scene.add(box);


                //Ammojs Section
                let transform = new Ammo.btTransform();
                transform.setIdentity();
                transform.setOrigin( new Ammo.btVector3( pos.x, pos.y, pos.z ) );
                transform.setRotation( new Ammo.btQuaternion( quat.x, quat.y, quat.z, quat.w ) );
                let motionState = new Ammo.btDefaultMotionState( transform );

                let colShape = new Ammo.btBoxShape( new Ammo.btVector3( scale.x * 0.5, scale.y * 0.5, scale.z * 0.5 ) );
                colShape.setMargin( 0.05 );

                let localInertia = new Ammo.btVector3( 0, 0, 0 );
                colShape.calculateLocalInertia( mass, localInertia );

                let rbInfo = new Ammo.btRigidBodyConstructionInfo( mass, motionState, colShape, localInertia );
                boxBody = new Ammo.btRigidBody( rbInfo );

                physicsWorld.addRigidBody( boxBody );
                
                // set up pivot point
                boxPivot = new Ammo.btVector3(5, 0, 0 );
            }
                    
            function createCylinder() // function creates motor cylinder
            {
                let pos = {x: 5, y: 3, z: 0};
                let scale = {x: 1, y: 5, z: 1};
                let quat = {x: 0, y: 0, z: 1, w: 1};
                let mass = 1;

                //threeJS Section
                Cylinder = new THREE.Mesh(new THREE.CylinderBufferGeometry(), new THREE.MeshPhongMaterial({color: 0x000000}));

                Cylinder.position.set(pos.x, pos.y, pos.z);
                Cylinder.scale.set(scale.x, scale.y, scale.z);
                Cylinder.rotation.set(quat.x, quat.y, quat.z, quat.w);
                
                Cylinder.castShadow = true;
                Cylinder.receiveShadow = true;

                scene.add(Cylinder);

                //Ammojs Section
                let transform = new Ammo.btTransform();
                transform.setIdentity();
                transform.setOrigin( new Ammo.btVector3( pos.x, pos.y, pos.z ) );
                transform.setRotation( new Ammo.btQuaternion( quat.x, quat.y, quat.z, quat.w ) );
                let motionState = new Ammo.btDefaultMotionState( transform );

                let colShape = new Ammo.btCylinderShape( new Ammo.btVector3( scale.x * 0.5, scale.y * 0.5, scale.z * 0.5 ) );
                colShape.setMargin( 0.05 );

                let localInertia = new Ammo.btVector3( 0, 0, 0 );
                colShape.calculateLocalInertia( mass, localInertia );

                let rbInfo = new Ammo.btRigidBodyConstructionInfo( mass, motionState, colShape, localInertia );
                cylinderBody = new Ammo.btRigidBody( rbInfo );
                
                //dynamic motion section
                cylinderBody.setFriction(4);
                cylinderBody.setRollingFriction(10);
                cylinderBody.setActivationState( STATE.DISABLE_DEACTIVATION );
                physicsWorld.addRigidBody( cylinderBody );
                Cylinder.userData.physicsBody = cylinderBody;
                rigidBodies.push(Cylinder);
                
                // set up pivot point
                cylinderPivot = new Ammo.btVector3( 0, 0 , 0);
                
                // define the joint between box and cylinder
                let p2p = new Ammo.btPoint2PointConstraint( cylinderBody, boxBody, cylinderPivot, boxPivot);
                physicsWorld.addConstraint( p2p, true );
                
            }
       
            function moveCylinder( deltaTime ) // function rotates cylinder based on keyboard input
            {
                let scalingFactor = 1; // changes speed of rotation
                //Get variables
                iPrev = i;					//Previous current value
                i = resistor.getCurrent();	//Current
                iDot = (i - iPrev)/deltaTime;	//Derivative of current
                v = source.getVoltageDiff();
                L = 0.5;	//Set value for now
                R = 1;      //Set value for now
                K = 1	// EMF and torque constant
                let angularV = (v - R*i - L*iDot)/K;	//Angular velocity
                let EMF = K*angularV;		//Back-EMF
                let resultantImpulse = new Ammo.btVector3( angularV, 0, 0 ) // rotation vector
                //resultantImpulse.op_mul(scalingFactor);	//Scale impulse
                let physicsBody = Cylinder.userData.physicsBody;
                physicsBody.setAngularVelocity( resultantImpulse );	//Set angular velocity
                backEMF.setVoltage(EMF);	//Set back-EMF
            }
                                                     
            function updatePhysics( deltaTime ) // function refreshes physics
            {
                // Step world
                physicsWorld.stepSimulation( deltaTime, 10 );

                // Update rigid bodies
                for ( let i = 0; i < rigidBodies.length; i++ )
                {
                    let objThree = rigidBodies[ i ];
                    let objAmmo = objThree.userData.physicsBody;
                    let ms = objAmmo.getMotionState();
                    if ( ms )
                    {
                        ms.getWorldTransform( tmpTrans );
                        let p = tmpTrans.getOrigin();
                        let q = tmpTrans.getRotation();
                        objThree.position.set( p.x(), p.y(), p.z() );
                        objThree.quaternion.set( q.x(), q.y(), q.z(), q.w() );
                    }
                }
            }