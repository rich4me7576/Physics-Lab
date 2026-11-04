package com.example.data.model

object LabCatalogData {

    val curriculumMatrix: List<CurriculumItem> = listOf(
        CurriculumItem(
            id = "curr_mech",
            domain = "1. Mechanics",
            subTopics = listOf("Motion", "Velocity & Acceleration", "Projectile Motion", "Circular Motion", "Newton's Laws", "Conservation Principles", "Equilibrium of Forces"),
            nerdcCode = "NERDC-PHY-SS1-M1 / SS2-M2",
            waecSyllabusRef = "WAEC Sec. I (1–9) & Practical Paper 3 Q1",
            necoSyllabusRef = "NECO Theme 1 & Practical Alternative A Q1",
            learningObjectives = listOf(
                "Determine acceleration due to gravity (g) using a simple pendulum and compound pendulum",
                "Verify Hooke's law and determine the force constant (k) of a helical spring",
                "Verify the principle of moments using a metre rule balanced on a knife edge",
                "Investigate static and dynamic coefficient of friction on an inclined plane"
            ),
            practicalActivities = listOf(
                "Simple Pendulum: Plot T² against L and deduce g from slope (4π²/g)",
                "Spiral Spring: Measure extension e for varying loads M; plot M against e",
                "Knife-Edge Moment Balance: Determine unknown mass Mx by balancing moments",
                "Inclined Plane: Measure limiting angle of repose θ and verify μ = tan(θ)"
            ),
            requiredEquipment = listOf("Metre rule", "Vernier calipers", "Micrometer screw gauge", "Stopwatch", "Pendulum bob", "Retort stand", "Knife edge", "Spring balance", "Slotted weights", "Pulleys", "Strings", "Inclined plane"),
            simulationRequirements = "Rigid-body 60Hz RK4 solver with air drag, string tension, restoring torque (-mgL sinθ), spring damping, and realistic human stopwatch reaction jitter (±0.06s).",
            assessmentMethods = listOf("Automated table verification (5–6 readings)", "Graph slope & intercept tolerance (±5%)", "Precautions & sources of error grading")
        ),
        CurriculumItem(
            id = "curr_energy",
            domain = "2. Energy, Work & Machines",
            subTopics = listOf("Work", "Power", "Energy Conversion", "Mechanical Advantage (MA)", "Velocity Ratio (VR)", "Efficiency of Simple Machines"),
            nerdcCode = "NERDC-PHY-SS1-E2 / SS2-E3",
            waecSyllabusRef = "WAEC Sec. I (10–12) & Paper 3",
            necoSyllabusRef = "NECO Theme 2 (Work, Energy & Power)",
            learningObjectives = listOf(
                "Determine Mechanical Advantage (MA), Velocity Ratio (VR), and Efficiency (η) of a block-and-tackle pulley system",
                "Demonstrate conservation of mechanical energy (PE ↔ KE) on an inclined track"
            ),
            practicalActivities = listOf(
                "Pulley System Efficiency: Measure effort E for load L; plot Efficiency η vs Load L",
                "Inclined Plane Work-Energy: Compare potential energy loss mgh with kinetic energy gain ½mv² + friction work"
            ),
            requiredEquipment = listOf("Pulleys", "Strings", "Spring balance", "Weights", "Inclined plane", "Metre rule", "Stopwatch"),
            simulationRequirements = "Pulley axle friction modelling, string inextensibility constraints, and dynamic load-effort curves.",
            assessmentMethods = listOf("Efficiency curve plotting", "Deduction of limiting efficiency", "Viva voce conceptual quiz")
        ),
        CurriculumItem(
            id = "curr_matter",
            domain = "3. Properties of Matter",
            subTopics = listOf("Density", "Relative Density", "Archimedes' Principle", "Law of Flotation", "Elasticity", "Hooke's Law", "Pressure in Fluids", "Viscosity"),
            nerdcCode = "NERDC-PHY-SS1-PM1 / SS2-PM2",
            waecSyllabusRef = "WAEC Sec. I (13–17) & Practical Q1",
            necoSyllabusRef = "NECO Theme 1 & 3 (Particulate Nature & Fluids)",
            learningObjectives = listOf(
                "Determine the density of irregular solids using Eureka can / measuring cylinder displacement",
                "Determine relative density of a liquid using Archimedes' principle and U-tube manometer",
                "Determine Young's modulus and elastic limit of a metallic wire"
            ),
            practicalActivities = listOf(
                "Relative Density by Upthrust: Weigh solid in air (W1), water (W2), and liquid (W3)",
                "Displacement Volume: Submerge density blocks in graduated measuring cylinder with meniscus reading"
            ),
            requiredEquipment = listOf("Beakers", "Measuring cylinder", "Density blocks", "Spring balance", "Vernier calipers", "Micrometer screw gauge"),
            simulationRequirements = "Buoyancy force calculation (F_b = ρ_fluid * V_submerged * g), concave/convex liquid meniscus rendering with parallax check.",
            assessmentMethods = listOf("Meniscus eye-level alignment check", "Zero-error correction on calipers/micrometer", "Upthrust ratio calculation")
        ),
        CurriculumItem(
            id = "curr_heat",
            domain = "4. Heat & Thermodynamics",
            subTopics = listOf("Thermal Expansion", "Specific Heat Capacity", "Latent Heat", "Calorimetry", "Heat Transfer (Conduction, Convection, Radiation)", "Gas Laws (Boyle's, Charles', Pressure)"),
            nerdcCode = "NERDC-PHY-SS1-H1 / SS2-H2",
            waecSyllabusRef = "WAEC Sec. II (18–23) & Practical Paper 3",
            necoSyllabusRef = "NECO Theme 2 (Thermal Physics)",
            learningObjectives = listOf(
                "Determine the specific heat capacity of a solid or liquid by method of mixtures and electrical calorimetry",
                "Verify Newton's law of cooling and plot cooling curves",
                "Verify Boyle's law (PV = k) and Charles' law (V/T = k)"
            ),
            practicalActivities = listOf(
                "Electrical Calorimetry: Heat water in copper calorimeter with heating coil (IVt = mcΔθ), plot θ vs time t",
                "Method of Mixtures: Transfer heated brass block from boiling water bath into lagged calorimeter"
            ),
            requiredEquipment = listOf("Thermometer", "Calorimeter", "Heating coil", "Bunsen burner", "Water bath", "Stopwatch", "Beakers"),
            simulationRequirements = "Real-time differential heat transfer (dQ/dt = IV - hA(T - T_amb)), thermal equilibrium solver, stirrer homogenization.",
            assessmentMethods = listOf("Cooling curve tangent gradient", "Precaution verification (lagging, stirring)", "Specific heat deduction")
        ),
        CurriculumItem(
            id = "curr_waves",
            domain = "5. Wave Motion & Sound",
            subTopics = listOf("Wave Properties (Reflection, Refraction, Diffraction, Interference)", "Sound Waves", "Resonance in Air Columns", "Vibrations in Strings (Sonometer)"),
            nerdcCode = "NERDC-PHY-SS2-W1 / SS2-W2",
            waecSyllabusRef = "WAEC Sec. III (24–27) & Practical Q2",
            necoSyllabusRef = "NECO Theme 3 (Waves — Motion Without Material Transfer)",
            learningObjectives = listOf(
                "Determine the speed of sound in air and end-correction using a resonance tube and set of tuning forks",
                "Investigate the relationship between frequency f and resonant length L (L = v/4f - c)",
                "Analyze wave amplitude, frequency, and phase on a virtual Cathode Ray Oscilloscope (CRO)"
            ),
            practicalActivities = listOf(
                "Resonance Tube: Measure first resonant length L1 for tuning forks (256Hz–512Hz); plot L1 against 1/f",
                "Oscilloscope Analysis: Measure peak-to-peak voltage and period from signal generator waveform"
            ),
            requiredEquipment = listOf("Tuning forks", "Resonance tube", "Signal generator", "Oscilloscope", "Metre rule", "Beakers"),
            simulationRequirements = "Acoustic standing wave synthesizer (real audio tone generation matching fork frequency + resonance amplitude peak at L = (2n-1)λ/4 - 0.6r).",
            assessmentMethods = listOf("Plot of L against 1/f", "Deduction of velocity of sound v = 4 * slope", "End correction c from y-intercept")
        ),
        CurriculumItem(
            id = "curr_light",
            domain = "6. Light & Optics",
            subTopics = listOf("Reflection at Plane & Curved Mirrors", "Refraction (Snell's Law, Refractive Index)", "Total Internal Reflection", "Dispersion by Triangular Prism", "Thin Lenses", "Optical Instruments"),
            nerdcCode = "NERDC-PHY-SS2-L1 / SS2-L2",
            waecSyllabusRef = "WAEC Sec. III (28–32) & Practical Paper 3 Q2",
            necoSyllabusRef = "NECO Theme 3 (Light Waves & Optical Instruments)",
            learningObjectives = listOf(
                "Determine the focal length f of a converging lens by plotting 1/v against 1/u or (u+v) against uv",
                "Trace light rays through a rectangular glass block and triangular prism using optical pins to find refractive index n",
                "Verify laws of reflection and locate images by no-parallax method"
            ),
            practicalActivities = listOf(
                "Convex Lens on Optical Bench: Measure image distance v for object distances u (20cm–60cm)",
                "Triangular Glass Prism: Measure angle of deviation D for varying angles of incidence i; plot D vs i to find D_min"
            ),
            requiredEquipment = listOf("Optical bench", "Convex lens", "Concave lens", "Plane mirror", "Prism", "Optical pins", "Screen", "Light source", "Metre rule"),
            simulationRequirements = "Real-time 2D/3D vector ray tracer (Snell's law n1 sinθ1 = n2 sinθ2, Gaussian lens equation 1/f = 1/u + 1/v, chromatic dispersion, image blur outside focal plane, pin parallax).",
            assessmentMethods = listOf("Ray trace angle accuracy (±1°)", "Focal length deduction from intercepts", "No-parallax alignment verification")
        ),
        CurriculumItem(
            id = "curr_elec",
            domain = "7. Electricity",
            subTopics = listOf("Electric Current", "Potential Difference (Voltage)", "Ohm's Law & Resistance", "Internal Resistance of a Cell", "Series & Parallel Networks", "Metre Bridge & Potentiometer", "Capacitors"),
            nerdcCode = "NERDC-PHY-SS1-EL1 / SS3-EL2",
            waecSyllabusRef = "WAEC Sec. IV (33–38) & Practical Paper 3 Q3",
            necoSyllabusRef = "NECO Theme 4 (Fields at Rest & in Motion — Electricity)",
            learningObjectives = listOf(
                "Verify Ohm's law and determine unknown resistance R and resistivity ρ of a wire",
                "Determine the EMF (E) and internal resistance (r) of a dry Leclanché/Daniel cell using ammeter-voltmeter and potentiometer",
                "Compare EMFs of two cells on a 100cm potentiometer and determine balance lengths"
            ),
            practicalActivities = listOf(
                "Potentiometer Balance: Jockey tap along 100cm constantan wire to find null deflection on galvanometer",
                "Internal Resistance Circuit: Vary resistance box / rheostat R, record V and I, plot 1/I against R",
                "Capacitor RC Discharge: Record voltage V(t) = V0 e^(-t/RC) across discharging capacitor"
            ),
            requiredEquipment = listOf("Cells", "Batteries", "Resistors", "Rheostat", "Galvanometer", "Ammeter", "Voltmeter", "Multimeter", "Potentiometer", "Capacitors", "Switches", "Connecting wires", "Breadboard"),
            simulationRequirements = "Modified Nodal Analysis (MNA) circuit solver computing node voltages, branch currents, jockey contact resistance, wire heating drift, and galvanometer needle deflection.",
            assessmentMethods = listOf("Circuit topology check before switch closure", "Null balance precision (±0.5cm)", "Slope & intercept grading")
        ),
        CurriculumItem(
            id = "curr_mag",
            domain = "8. Magnetism & Electromagnetism",
            subTopics = listOf("Magnetic Fields & Flux", "Earth's Magnetic Field", "Electromagnetism", "Fleming's Rules", "Faraday's & Lenz's Laws", "Electric Motors", "AC/DC Generators", "Transformers"),
            nerdcCode = "NERDC-PHY-SS3-MG1 / SS3-MG2",
            waecSyllabusRef = "WAEC Sec. IV (39–44)",
            necoSyllabusRef = "NECO Theme 4 (Magnetic & Electromagnetic Fields)",
            learningObjectives = listOf(
                "Plot magnetic field lines around bar magnets and current-carrying solenoids using compass needles",
                "Verify Faraday's law of electromagnetic induction (E = -N dΦ/dt)",
                "Investigate transformer voltage-turns ratio (Vs/Vp = Ns/Np) and eddy current losses"
            ),
            practicalActivities = listOf(
                "Solenoid Induction: Plunge bar magnet at controlled velocity into solenoid connected to center-zero galvanometer",
                "Transformer Efficiency: Vary primary AC voltage Vp and secondary turns Ns; plot Vs vs Ns"
            ),
            requiredEquipment = listOf("Bar magnets", "Solenoids", "Electromagnets", "Compass needles", "Galvanometer", "Voltmeter", "Ammeter", "Connecting wires"),
            simulationRequirements = "Biot-Savart & dipole vector field solver, dynamic flux linkage derivative dΦ/dt for induced EMF.",
            assessmentMethods = listOf("Field line polarity check", "Peak deflection vs plunge velocity plot", "Transformer efficiency calculation")
        ),
        CurriculumItem(
            id = "curr_modern",
            domain = "9. Modern & Atomic Physics",
            subTopics = listOf("Atomic Structure", "Photoelectric Effect", "Thermionic Emission", "X-Rays", "Radioactivity (Alpha, Beta, Gamma)", "Half-Life & Nuclear Reactions", "Semiconductor Electronics (Diodes, Transistors)"),
            nerdcCode = "NERDC-PHY-SS3-MP1 / SS3-MP2",
            waecSyllabusRef = "WAEC Sec. V (45–52)",
            necoSyllabusRef = "NECO Theme 5 (Energy Quantization & Duality of Matter)",
            learningObjectives = listOf(
                "Plot I-V characteristics of a p-n junction semiconductor diode in forward and reverse bias",
                "Simulate radioactive decay (N(t) = N0 e^(-λt)) and deduce half-life T_1/2 from semi-log plots",
                "Determine Planck's constant h from photoelectric stopping potential Vs against frequency f"
            ),
            practicalActivities = listOf(
                "Semiconductor Diode Curve: Breadboard circuit with variable DC supply, milliammeter, and voltmeter",
                "Half-Life Monte Carlo: Count decays per time interval with background radiation correction"
            ),
            requiredEquipment = listOf("Breadboard", "Multimeter", "Voltmeter", "Ammeter", "Resistors", "Switches", "Connecting wires"),
            simulationRequirements = "Shockley diode equation (I = I_s(e^(V/nV_T) - 1)), Poisson stochastic decay engine.",
            assessmentMethods = listOf("Knee voltage identification", "Semi-log graph plotting", "Half-life calculation")
        ),
        CurriculumItem(
            id = "curr_comm",
            domain = "10. Communication Physics & Digital Systems",
            subTopics = listOf("Digital Logic Gates (AND, OR, NOT, NAND, NOR, XOR)", "Electromagnetic Spectrum", "Amplitude & Frequency Modulation (AM/FM)", "Optical Fibre & Satellite Telecommunications", "Signal Transmission"),
            nerdcCode = "NERDC-PHY-SS3-CP1",
            waecSyllabusRef = "WAEC Sec. V (Electronics & Logic Gates)",
            necoSyllabusRef = "NECO Theme 5 (Physics in Technology & Communication)",
            learningObjectives = listOf(
                "Construct and verify truth tables for combinational logic gates on a virtual breadboard",
                "Visualize carrier wave modulation (AM/FM) and signal attenuation on a dual-trace oscilloscope"
            ),
            practicalActivities = listOf(
                "Logic Gate Truth Table Verification: Toggle binary inputs A, B (0V / 5V) and measure output LED & voltage",
                "AM/FM Signal Demodulation: Adjust carrier and modulating frequencies on signal generator + CRO"
            ),
            requiredEquipment = listOf("Breadboard", "Signal generator", "Oscilloscope", "Multimeter", "Switches", "Connecting wires"),
            simulationRequirements = "Boolean logic propagation with gate delay, FFT spectrum & time-domain waveform rendering.",
            assessmentMethods = listOf("Truth table completion", "Modulation index calculation")
        )
    )

    val equipmentInventory: List<VirtualEquipment> = listOf(
        // MECHANICS (15 items)
        VirtualEquipment("eq_metre_rule", "Metre Rule (Hardwood / Brass-tipped)", "Mechanics", "100.0 × 2.5 × 0.5 cm", 120.0, "±0.05 cm (0.5 mm)", "±0.05 cm end-wear offset", "Rigid beam with uniform mass distribution (CG at 50.0 cm ± 0.2 cm); supports knife-edge pivot and torque moments.", "Inspect 0.0 cm and 100.0 cm brass ends; record end-wear correction before taking readings."),
        VirtualEquipment("eq_vernier", "Vernier Calipers (Steel)", "Mechanics", "18.0 × 6.2 × 0.8 cm", 185.0, "±0.01 cm (0.1 mm)", "±0.03 cm jaw zero error", "Sliding Vernier scale with 10 divisions = 9 mm; internal/external jaws and depth blade with thumb-lock.", "Close jaws completely without object; read Vernier coincidence to determine positive or negative zero error."),
        VirtualEquipment("eq_micrometer", "Micrometer Screw Gauge", "Mechanics", "12.5 × 5.5 × 1.8 cm", 240.0, "±0.001 cm (0.01 mm)", "±0.02 mm thimble zero error", "0.5 mm pitch precision screw with 50-division circular thimble and ratchet torque limiter (clicks at 3 N).", "Turn ratchet until anvil and spindle meet (3 clicks); record thimble offset from datum line."),
        VirtualEquipment("eq_stopwatch", "Digital & Mechanical Laboratory Stopwatch", "Mechanics", "7.5 × 6.0 × 2.0 cm", 95.0, "±0.01 s (digital) / ±0.1 s (analog)", "0.00 s reset", "High-precision quartz oscillator with start/stop/lap triggers and simulated human reaction latency (±0.06 s).", "Press Reset button to verify 00:00.00 display prior to timing 20 oscillations."),
        VirtualEquipment("eq_pendulum_bob", "Solid Brass Pendulum Bob (Spherical)", "Mechanics", "2.0 cm diameter", 50.0, "±0.1 g mass / ±0.01 cm diameter", "None", "High-density (8.5 g/cm³) spherical bob with top hook; aerodynamic drag C_d = 0.47.", "Measure diameter with Vernier calipers along two perpendicular axes to locate center of gravity (r = d/2)."),
        VirtualEquipment("eq_retort_stand", "Cast-Iron Retort Stand, Bosshead & Split Cork Clamp", "Mechanics", "60.0 × 20.0 × 12.0 cm", 1850.0, "±0.1 cm clamp height", "None", "Heavy cast-iron base for low center of gravity; bosshead rotates 360° and locks split cork firmly.", "Place on level bench; tighten bosshead thumb-screw and ensure split cork grips thread without slipping."),
        VirtualEquipment("eq_knife_edge", "Hardened Steel / Hardwood Knife Edge Fulcrum", "Mechanics", "10.0 × 4.0 × 6.5 cm", 210.0, "±0.05 cm pivot line", "None", "Sharp 45° wedge prism providing near-zero rotational friction for moment of forces experiments.", "Set on flat horizontal bench; balance unloaded metre rule first to determine actual center of gravity G."),
        VirtualEquipment("eq_spring_balance", "Newton & Gram Helical Spring Balance (0–10 N / 0–1000 g)", "Mechanics", "24.0 × 3.5 × 2.2 cm", 140.0, "±0.1 N / ±10 g", "±0.2 N zero nut adjustment", "Linear Hookean steel spring (k = 125 N/m) with zero-adjust top nut and load hook.", "Hold vertically unloaded; turn top knurled nut until pointer aligns with 0.0 N mark."),
        VirtualEquipment("eq_weights", "Slotted Brass Masses & Hanger Set (10g, 20g, 50g, 100g)", "Mechanics", "4.0 cm diameter × 12.0 cm stack", 500.0, "±0.1 g (Class M2)", "None", "Precision-machined brass slotted discs stacking onto 50 g hanger; applies downward gravitational force mg.", "Verify stamped mass values on spring balance before mounting."),
        VirtualEquipment("eq_pulleys", "Single & Double Sheave Block-and-Tackle Pulleys", "Mechanics", "11.0 × 4.5 × 3.0 cm", 160.0, "VR = 1 to 6", "Axle friction torque 0.015 N·m", "Low-friction ball-bearing aluminum sheaves with top & bottom hooks for mechanical advantage setups.", "Oil bearings virtually (toggle low/normal friction) and ensure string runs in sheave grooves."),
        VirtualEquipment("eq_strings", "Inextensible Cotton & Nylon Laboratory Twine", "Mechanics", "150.0 × 0.1 cm", 5.0, "±0.05 cm length", "Negligible stretch", "Massless inextensible constraint line when tension T > 0; goes slack if T ≤ 0.", "Measure effective length from split cork bottom to center of pendulum bob under tension."),
        VirtualEquipment("eq_beakers", "Borosilicate Glass Beakers (250 mL & 500 mL)", "Mechanics", "12.0 × 9.0 cm", 135.0, "±5 mL graduation", "None", "Heat-resistant Pyrex glass vessel for water displacement, upthrust immersion, and heating.", "Place on flat surface and tare mass if weighing liquid contents."),
        VirtualEquipment("eq_measuring_cylinder", "Graduated Measuring Cylinder (100 mL & 250 mL)", "Mechanics", "26.0 × 4.5 cm", 110.0, "±0.5 mL (100 mL cylinder)", "0.0 mL base", "Tall transparent cylinder displaying realistic concave water meniscus.", "Read bottom of concave meniscus at horizontal eye level to eliminate parallax error."),
        VirtualEquipment("eq_density_blocks", "Regular & Irregular Density Blocks (Brass, Aluminum, Iron, Wood)", "Mechanics", "4.0 × 3.0 × 2.0 cm", 204.0, "±0.01 g/cm³", "None", "Calibrated material densities (Brass 8.5, Al 2.7, Fe 7.87, Oak 0.72 g/cm³) for Archimedes immersion.", "Dry block thoroughly before weighing in air."),
        VirtualEquipment("eq_inclined_plane", "Hinged Hardwood Inclined Plane with Protractor & Roller", "Mechanics", "65.0 × 12.0 × 4.0 cm", 1450.0, "±0.5° angle / ±0.1 cm length", "0.0° horizontal level", "Adjustable ramp (0°–60°) with configurable surface materials (wood, glass, sandpaper) and top pulley.", "Level base using spirit bubble and verify protractor reads 0.0° when flat."),

        // ELECTRICITY (13 items)
        VirtualEquipment("eq_cells", "Dry Leclanché & Daniel Primary Cells (1.5 V & 1.08 V)", "Electricity", "6.0 × 3.3 cm", 95.0, "E = 1.50 V ± 0.01 V, r = 0.5–2.0 Ω", "None", "Electrochemical voltage source with realistic internal resistance drop (V = E - Ir) and polarization.", "Measure open-circuit EMF with high-impedance voltmeter before connecting load."),
        VirtualEquipment("eq_batteries", "Heavy-Duty 2V–12V DC Accumulator / Battery Pack", "Electricity", "15.0 × 10.0 × 9.5 cm", 1200.0, "±0.02 V stability", "None", "Low-internal-resistance (r = 0.05 Ω) driver cell for potentiometer and bridge circuits.", "Verify terminal polarity (Red +, Black -) and set output tap voltage."),
        VirtualEquipment("eq_resistors", "Standard Decade Resistance Box (1 Ω – 1000 Ω) & Fixed Resistors", "Electricity", "18.0 × 12.0 × 8.0 cm", 680.0, "±0.5% manganin coils", "0.02 Ω plug contact resistance", "Brass shorting plugs on non-inductive manganin coils; removing a plug inserts its marked resistance.", "Give all brass plugs a firm twist to minimize contact resistance before taking readings."),
        VirtualEquipment("eq_rheostat", "Sliding-Contact Wire-Wound Rheostat (0–50 Ω, 5 A)", "Electricity", "28.0 × 8.5 × 10.0 cm", 820.0, "±0.1 Ω continuous slider", "0.0 Ω at minimum end", "Oxidized Eureka wire wound on ceramic tube with top sliding contact bar.", "Connect one lower terminal and the top slider terminal for variable resistance mode."),
        VirtualEquipment("eq_galvanometer", "Center-Zero Moving-Coil Galvanometer (-30–0–30 div)", "Electricity", "11.0 × 10.0 × 9.0 cm", 350.0, "20 µA/div sensitivity, R_g = 50 Ω", "±0.5 div zero-adjust screw", "Bi-directional pointer deflecting proportionally to current; equipped with protective jockey shunt.", "Adjust front mechanical zero-screw until needle rests exactly on 0 center mark."),
        VirtualEquipment("eq_ammeter", "Dual-Range Analog Moving-Coil Ammeter (0–1 A / 0–3 A)", "Electricity", "11.0 × 10.0 × 9.0 cm", 360.0, "±0.02 A (1A range)", "±0.02 A zero screw", "Low shunt resistance (0.05 Ω) current meter with anti-parallax mirror scale.", "Connect strictly in series; align eye so pointer image in mirror is hidden behind pointer."),
        VirtualEquipment("eq_voltmeter", "Dual-Range Analog Moving-Coil Voltmeter (0–3 V / 0–15 V)", "Electricity", "11.0 × 10.0 × 9.0 cm", 360.0, "±0.05 V (3V range)", "±0.05 V zero screw", "High internal resistance (10 kΩ/V) potential difference meter with anti-parallax mirror.", "Connect in parallel across component; verify zero reading with leads open."),
        VirtualEquipment("eq_multimeter", "Auto-Ranging Digital Multimeter (V, A, Ω, C)", "Electricity", "16.0 × 8.0 × 4.0 cm", 280.0, "±0.1% + 1 digit", "Auto-zero", "3½-digit LCD readout for DC/AC voltage, current, resistance, continuity buzzer, and diode test.", "Short test leads in Ω mode to verify 0.00 Ω lead resistance."),
        VirtualEquipment("eq_potentiometer", "100 cm Constantan Wire Potentiometer / Metre Bridge Board", "Electricity", "110.0 × 15.0 × 3.0 cm", 1600.0, "±0.05 cm balance length", "±0.1 cm end-correction", "Uniform cross-section constantan wire (R = 4.0 Ω/m) stretched over metre scale with knife-edge jockey.", "Tap jockey gently at 0 cm and 100 cm ends to confirm opposite galvanometer deflections."),
        VirtualEquipment("eq_capacitors", "Electrolytic & Film Capacitors (100 µF, 470 µF, 1000 µF, 2200 µF)", "Electricity", "4.5 × 2.5 cm", 45.0, "±5% capacitance tolerance", "0.0 V after shorting", "Stores charge Q = CV; discharges exponentially V(t) = V_0 e^(-t/RC).", "Short-circuit capacitor terminals with a wire before starting to ensure zero initial charge."),
        VirtualEquipment("eq_switches", "One-Way & Two-Way Plug Key Switches", "Electricity", "8.0 × 5.0 × 4.0 cm", 120.0, "R_contact < 0.01 Ω", "Infinite R when open", "Heavy brass blocks with tapered ebonite-handle brass plug.", "Remove plug key immediately after each reading to prevent cell polarization and wire heating."),
        VirtualEquipment("eq_wires", "PVC-Insulated Copper Connecting Leads with Crocodile Clips", "Electricity", "50.0 × 0.3 cm", 25.0, "R_lead = 0.01 Ω", "None", "Color-coded flexible copper wires with 4mm banana plugs and crocodile clips.", "Tighten all binding post terminals to prevent erratic resistance fluctuations."),
        VirtualEquipment("eq_breadboard", "Solderless Prototyping Breadboard (830 Tie-Points)", "Electricity", "16.5 × 5.5 × 1.0 cm", 90.0, "2.54 mm pitch", "None", "Spring-clip phosphor-bronze bus strips and terminal rows for electronics & logic gate experiments.", "Verify power rail continuity before inserting diodes, resistors, or ICs."),

        // OPTICS (8 items)
        VirtualEquipment("eq_optical_bench", "150 cm Graduated Twin-Rail Optical Bench with Riders", "Optics", "150.0 × 12.0 × 8.0 cm", 2800.0, "±0.05 cm rider index", "±0.1 cm index correction", "Rigid anodized aluminum rail with sliding riders holding light source, lens holders, and screen.", "Measure index offset between lens optical center and rider index mark using a standard rod."),
        VirtualEquipment("eq_convex_lens", "Biconvex Glass Lenses (f = +10 cm, +15 cm, +20 cm)", "Optics", "5.0 cm diameter", 65.0, "±0.1 cm focal length", "None", "Crown glass converging lens (n = 1.52) mounted in circular brass holder.", "Obtain rough focal length first by focusing a distant laboratory window onto a screen."),
        VirtualEquipment("eq_concave_lens", "Biconcave Diverging Glass Lens (f = -15 cm)", "Optics", "5.0 cm diameter", 60.0, "±0.1 cm focal length", "None", "Crown glass diverging lens forming virtual, erect, diminished images.", "Clean lens surfaces with lint-free tissue and align optical axis perpendicular to bench."),
        VirtualEquipment("eq_plane_mirror", "Silvered Glass Plane Mirror with Wooden Support Block", "Optics", "10.0 × 7.5 × 0.4 cm", 85.0, "±0.5° angular alignment", "Back-silvering thickness 0.2 cm", "98% specular reflectance flat mirror for reflection laws and auto-collimation.", "Ensure mirror stands strictly vertical (90° to drawing board) using a set-square."),
        VirtualEquipment("eq_prism", "Equilateral Triangular Glass Prism (60°–60°–60°, n = 1.50)", "Optics", "5.0 × 5.0 × 5.0 cm", 145.0, "±0.5° apex angle A = 60.0°", "None", "Optically polished flint/crown glass prism for refraction and minimum deviation experiments.", "Trace prism outline sharply with a fine pencil before placing optical pins."),
        VirtualEquipment("eq_pins", "Tall Brass Optical Pins & Softwood Drawing Board", "Optics", "5.0 cm height × 0.1 cm", 3.0, "±0.1 cm pin spacing", "None", "Straight Nickel-plated steel pins placed ≥ 5 cm apart on A3 white sheet for ray tracing.", "Ensure pins are strictly vertical and view their bases (feet) when aligning in a straight line."),
        VirtualEquipment("eq_screen", "White Matte Translucent Ground-Glass Image Screen", "Optics", "15.0 × 15.0 cm", 150.0, "±0.1 cm sharp focus position", "None", "High-contrast matte white surface with millimeter cross-grid for measuring magnification.", "Slide screen slowly back and forth to bracket the position of sharpest image focus."),
        VirtualEquipment("eq_light_source", "Illuminated Cross-Wire Object Box (12 V Halogen / LED)", "Optics", "10.0 × 8.0 × 8.0 cm", 310.0, "Point / Cross-wire object", "None", "Circular aperture with fine metallic cross-wire illuminated from behind.", "Align cross-wire center at the exact same height above bench as the lens optical center."),

        // HEAT (5 items)
        VirtualEquipment("eq_thermometer", "Mercury-in-Glass Thermometer (-10°C to 110°C)", "Heat", "30.0 × 0.6 cm", 35.0, "±0.5°C (0.5°C/div)", "±0.2°C ice-point offset", "Fast-response bulb thermometer with thermal time constant τ = 2.5 s.", "Check 0.0°C in melting pure ice and 100.0°C in steam; stir liquid continuously before reading."),
        VirtualEquipment("eq_calorimeter", "Lagged Copper Calorimeter with Copper Stirrer & Lid", "Heat", "10.0 × 7.0 cm", 115.0, "c_Cu = 400 J/(kg·K)", "None", "Polished inner copper vessel seated inside cotton-wool lagging jacket to minimize radiative/convective loss.", "Weigh clean, dry inner calorimeter + stirrer before adding water."),
        VirtualEquipment("eq_heating_coil", "Constantan Immersion Heating Coil (12 V, 24 W, 6 Ω)", "Heat", "12.0 × 4.0 cm", 75.0, "±0.05 W power dissipation", "None", "Submersible resistance coil converting electrical energy IVt into thermal energy.", "Ensure heating coil is completely submerged in liquid before switching on current."),
        VirtualEquipment("eq_bunsen_burner", "Laboratory Gas Bunsen Burner with Air-Hole Collar, Tripod & Gauze", "Heat", "15.0 × 8.0 cm", 290.0, "200 W – 1200 W adjustable flame", "None", "Adjustable air collar transitioning between luminous safety flame and roaring blue non-luminous flame.", "Open air collar for clean blue flame when heating water bath."),
        VirtualEquipment("eq_water_bath", "Thermostatic Boiling Water Bath (1000 mL)", "Heat", "20.0 × 16.0 cm", 850.0, "±0.5°C steady bath", "None", "Maintains solid metal specimens at 100.0°C thermal equilibrium prior to rapid transfer.", "Keep solid suspended in boiling water for at least 5 minutes before quick transfer to calorimeter."),

        // WAVE (4 items)
        VirtualEquipment("eq_tuning_forks", "Set of 8 Calibrated Steel Tuning Forks (256 Hz – 512 Hz) + Rubber Pad", "Waves", "16.0 × 2.5 × 0.8 cm", 70.0, "±0.2 Hz stamped frequency", "None", "Precision acoustic resonators (256, 288, 320, 341.3, 384, 426.6, 480, 512 Hz).", "Strike prong gently on rubber pad only (never on hard bench) and hold ~1 cm above resonance tube."),
        VirtualEquipment("eq_resonance_tube", "Glass Resonance Tube (100 cm) with Adjustable Water Reservoir", "Waves", "100.0 × 3.4 cm (r = 1.7 cm)", 920.0, "±0.1 cm air column length", "End correction c ≈ 0.6r = 1.02 cm", "Variable-length closed air column; produces sharp acoustic amplification when L + c = λ/4.", "Raise water level near top and lower slowly to detect the exact first position of loudest resonance."),
        VirtualEquipment("eq_signal_gen", "Digital Sine / Square / Triangle Audio Signal Generator (1 Hz – 100 kHz)", "Waves", "22.0 × 14.0 × 10.0 cm", 1100.0, "±0.01 Hz digital synthesizer", "0.0 V DC offset", "Variable frequency and amplitude (0–10 Vpp) function generator with speaker & BNC outputs.", "Verify amplitude knob is at minimum before connecting to loudspeaker or oscilloscope."),
        VirtualEquipment("eq_oscilloscope", "Dual-Trace Cathode Ray Oscilloscope (CRO) with Timebase & Y-Gain", "Waves", "32.0 × 24.0 × 18.0 cm", 3400.0, "±2% calibrated graticule (1 cm/div)", "0 V ground baseline", "Displays voltage-time waveform with adjustable Volts/div (0.1–5 V/div) and Time/div (0.1–10 ms/div).", "Switch input coupling to GND and center trace on horizontal axis before measuring signal."),

        // MAGNETISM (4 items)
        VirtualEquipment("eq_bar_magnets", "Pair of Alnico Bar Magnets (N-S Painted) with Soft-Iron Keepers", "Magnetism", "10.0 × 2.0 × 1.0 cm", 160.0, "B_surface = 0.12 Tesla", "None", "Permanent dipole magnets generating 3D magnetic flux density B(r); supports attraction/repulsion.", "Store in pairs with opposite poles adjacent and soft-iron keepers across ends when not in use."),
        VirtualEquipment("eq_solenoids", "Multi-Tap Air & Iron-Core Solenoid Coils (200, 400, 800 Turns)", "Magnetism", "15.0 × 5.0 cm", 420.0, "N = 200 / 400 / 800 ± 1 turn", "None", "Enamelled copper coil generating uniform internal field B = µ_0 µ_r n I or induced EMF E = -N dΦ/dt.", "Remove external magnets and iron objects from bench before magnetic field experiments."),
        VirtualEquipment("eq_electromagnets", "U-Shaped Soft-Iron Electromagnet with Variable DC Excitation", "Magnetism", "14.0 × 10.0 × 4.0 cm", 780.0, "0.0 – 0.8 Tesla", "Remanence < 0.005 T", "High-permeability laminated soft-iron core magnetizing proportionally to current I.", "Demagnetize core using decreasing AC current if residual magnetism is detected."),
        VirtualEquipment("eq_compass", "Magnetic Plotting Compass & Deflection Magnetometer", "Magnetism", "3.0 cm & 12.0 cm diameter", 30.0, "±0.5° needle orientation", " Earth horizontal field B_H = 3.2×10⁻⁵ T", "Pivoted magnetized needle aligning along resultant vector B_total = B_Earth + B_magnet.", "Tap glass lightly before reading to overcome pivot static friction.")
    )

    val practicalExperiments: List<PracticalExperimentSpec> = listOf(
        PracticalExperimentSpec(
            id = "mech_pendulum",
            title = "WAEC/NECO Q1: Simple Pendulum — Determination of Acceleration Due to Gravity (g)",
            category = "Mechanics",
            examBoard = "WAEC",
            yearSpecimen = "WAEC 2025/2026 Specimen Paper 3 Q1",
            simType = SimulationType.PENDULUM_GRAVITY,
            aim = "To determine the acceleration due to gravity (g) at the laboratory and the effective length correction using a simple pendulum.",
            apparatus = listOf("Retort stand & clamp", "Split cork", "Inextensible cotton thread", "Brass pendulum bob", "Metre rule", "Stopwatch", "Vernier calipers"),
            theoryFormula = "T = 2π √(L / g)  ⇒  T² = (4π² / g) L",
            targetSlope = 4.028,
            slopeUnit = "s²/m",
            xLabel = "Length L (m)",
            yLabel = "Period Squared T² (s²)",
            procedureSteps = listOf(
                "1. Measure the diameter d of the pendulum bob with Vernier calipers and attach the thread through the split cork in the retort clamp.",
                "2. Adjust the pendulum length to L = 0.40 m (40.0 cm) measured from the bottom of the split cork to the center of the bob.",
                "3. Displace the bob through a small angle (θ ≤ 8°) so it oscillates in a single vertical plane and release.",
                "4. Measure the time t₁ and t₂ for 20 complete oscillations using the stopwatch; compute mean t and period T = t/20, then T².",
                "5. Repeat the procedure for L = 0.50 m, 0.60 m, 0.70 m, 0.80 m, and 0.90 m. Tabulate L, t₁, t₂, mean t, T, and T².",
                "6. Plot a graph of T² on the vertical axis against L on the horizontal axis, determine the slope s, and evaluate g = 4π²/s."
            ),
            standardPrecautions = listOf(
                "Avoided conical oscillation by displacing the bob through a small angle (θ < 10°) in a single vertical plane.",
                "Avoided parallax error when reading the metre rule by viewing graduations perpendicularly at eye level.",
                "Ensured the retort stand clamp and split cork were firmly tightened to prevent thread slippage during oscillation.",
                "Switched off laboratory ceiling fans and closed windows to minimize air draught resistance."
            ),
            shortAnswerQuestions = listOf(
                "(b)(i) State two precautions taken to ensure accurate results. [2 marks]",
                "(b)(ii) Explain why the bob is displaced through a small angle during the experiment. [2 marks]",
                "(b)(iii) From your graph, deduce the value of L when T = 1.50 s. [2 marks]"
            ),
            parameter1Label = "Pendulum Length L",
            parameter1Min = 0.30f,
            parameter1Max = 1.10f,
            parameter1Default = 0.60f,
            parameter1Unit = "m",
            parameter2Label = "Release Angle θ",
            parameter2Min = 3f,
            parameter2Max = 15f,
            parameter2Default = 8f,
            parameter2Unit = "deg"
        ),
        PracticalExperimentSpec(
            id = "mech_projectile",
            title = "WAEC/NECO Mechanics: 3D Projectile Motion — Range, Max Height & Trajectory",
            category = "Mechanics",
            examBoard = "WAEC",
            yearSpecimen = "WAEC/NECO Mechanics Ballistics & Kinematics Practical",
            simType = SimulationType.PROJECTILE_MOTION,
            aim = "To investigate 3D projectile motion, verify R = (u² sin 2θ)/g, and determine the launch velocity u and acceleration due to gravity g from the graph of Range R against sin(2θ).",
            apparatus = listOf("3D Spring Ballistics Launcher", "Steel Projectile Sphere (100 g)", "Protractor Plumb-Line (15°–80°)", "Carbon Impact Target Paper", "Metre Rule / Measuring Tape", "Digital Photogate Timer"),
            theoryFormula = "R = (u² / g) sin(2θ)  |  H_max = (u² sin²θ) / (2g)  |  T = (2u sinθ) / g",
            targetSlope = 22.96,
            slopeUnit = "m",
            xLabel = "sin(2θ)",
            yLabel = "Horizontal Range R (m)",
            procedureSteps = listOf(
                "1. Mount the 3D spring projectile launcher at bench level (y₀ = 0) and set the launch speed to u = 15.0 m/s.",
                "2. Set the launch angle θ = 15° using the built-in protractor and fire the steel sphere onto the carbon target strip.",
                "3. Measure the horizontal range R (m), maximum height H_max (m), and time of flight T (s).",
                "4. Repeat for θ = 20°, 25°, 30°, 35°, 40°, and 45°. Tabulate θ, 2θ, sin(2θ), H_max, T, and R.",
                "5. Plot a graph of Horizontal Range R (m) on the vertical axis against sin(2θ) on the horizontal axis and deduce g = u² / slope."
            ),
            standardPrecautions = listOf(
                "Ensured the projectile launcher base was clamped horizontally and leveled before firing.",
                "Took multiple launches at each angle θ and measured to the center of the carbon impact cluster.",
                "Avoided parallax error when reading the protractor launch angle θ and measuring tape."
            ),
            shortAnswerQuestions = listOf(
                "(b)(i) At what launch angle θ does a projectile achieve maximum horizontal range in the absence of air resistance? [2 marks]",
                "(b)(ii) State two factors that cause the actual range in air to be slightly less than u²sin(2θ)/g. [2 marks]",
                "(b)(iii) Calculate the maximum height H_max when u = 15.0 m/s and θ = 45°. [2 marks]"
            ),
            parameter1Label = "Launch Angle θ",
            parameter1Min = 15f,
            parameter1Max = 75f,
            parameter1Default = 45f,
            parameter1Unit = "deg",
            parameter2Label = "Launch Speed u",
            parameter2Min = 8f,
            parameter2Max = 22f,
            parameter2Default = 15f,
            parameter2Unit = "m/s"
        ),
        PracticalExperimentSpec(
            id = "opt_convex_lens",
            title = "WAEC/NECO Q2: Optics — Focal Length of a Converging Lens (Optical Bench)",
            category = "Light",
            examBoard = "NECO",
            yearSpecimen = "NECO 2025/2026 Practical Paper 3 Q2",
            simType = SimulationType.CONVEX_LENS_OPTICS,
            aim = "To determine the focal length f of a biconvex converging lens by plotting 1/v against 1/u on an optical bench.",
            apparatus = listOf("Optical bench (150 cm)", "Illuminated cross-wire object box", "Biconvex converging lens (f = 15.0 cm) in holder", "White image screen", "Metre rule"),
            theoryFormula = "1/f = 1/u + 1/v  ⇒  1/v = -1/u + 1/f",
            targetSlope = -1.00,
            slopeUnit = "dimensionless",
            xLabel = "1/u (cm⁻¹)",
            yLabel = "1/v (cm⁻¹)",
            procedureSteps = listOf(
                "1. Determine an approximate focal length f₀ of the converging lens by focusing a distant window onto the screen.",
                "2. Mount the illuminated object box at the 0.0 cm mark of the optical bench and place the lens at u = 20.0 cm.",
                "3. Adjust the white screen on the other side of the lens until a sharply focused inverted image of the cross-wire is formed.",
                "4. Measure and record the image distance v from the optical center of the lens to the screen.",
                "5. Repeat for u = 25.0, 30.0, 35.0, 40.0, and 50.0 cm. Tabulate u, v, u⁻¹ (cm⁻¹), and v⁻¹ (cm⁻¹) to 3 significant figures.",
                "6. Plot a graph of v⁻¹ on the vertical axis against u⁻¹ on the horizontal axis; read both intercepts I₁ and I₂ and evaluate f = 2 / (I₁ + I₂)."
            ),
            standardPrecautions = listOf(
                "Ensured the optical center of the lens, illuminated cross-wire, and center of screen were aligned along the same horizontal principal axis.",
                "Avoided parallax error when reading rider positions on the optical bench scale.",
                "Cleaned the lens surface carefully and shielded the screen from direct ambient room light to obtain a sharp image focus."
            ),
            shortAnswerQuestions = listOf(
                "(b)(i) State two precautions taken to obtain sharp and accurate readings. [2 marks]",
                "(b)(ii) Why can a real image not be formed on the screen when u < f? [2 marks]",
                "(b)(iii) Calculate the linear magnification M when u = 30.0 cm. [2 marks]"
            ),
            parameter1Label = "Object Distance u",
            parameter1Min = 18f,
            parameter1Max = 60f,
            parameter1Default = 25f,
            parameter1Unit = "cm",
            parameter2Label = "Focal Length f",
            parameter2Min = 10f,
            parameter2Max = 20f,
            parameter2Default = 15f,
            parameter2Unit = "cm"
        ),
        PracticalExperimentSpec(
            id = "elec_potentiometer",
            title = "WAEC/NECO Q3: Electricity — EMF & Internal Resistance via Potentiometer / Ammeter-Voltmeter",
            category = "Electricity",
            examBoard = "WAEC",
            yearSpecimen = "WAEC 2025/2026 Practical Paper 3 Q3",
            simType = SimulationType.POTENTIOMETER_OHMS_LAW,
            aim = "To determine the internal resistance r and electromotive force E of a dry cell using a standard decade resistance box, ammeter, and voltmeter.",
            apparatus = listOf("Dry Leclanché cell (E = 1.50 V)", "Decade resistance box R (1–20 Ω)", "Analog Ammeter (0–1 A)", "Analog Voltmeter (0–3 V)", "Plug key switch K", "Connecting copper wires"),
            theoryFormula = "E = I(R + r)  ⇒  R = E(1/I) - r",
            targetSlope = 1.50,
            slopeUnit = "V (Ω·A)",
            xLabel = "Reciprocal Current 1/I (A⁻¹)",
            yLabel = "Resistance R (Ω)",
            procedureSteps = listOf(
                "1. Connect the dry cell, plug key K, ammeter A, and resistance box R in series, with voltmeter V connected in parallel across R.",
                "2. With key K open, read and record the open-circuit electromotive force E₀ on the voltmeter.",
                "3. Remove plug R = 2.0 Ω from the resistance box, close key K briefly, and record the current I and terminal p.d. V. Open key K immediately.",
                "4. Repeat for R = 4.0, 6.0, 8.0, 10.0, and 12.0 Ω. Tabulate R, I, V, and I⁻¹ (A⁻¹).",
                "5. Plot a graph of R on the vertical axis against I⁻¹ on the horizontal axis.",
                "6. Determine the slope s (= E) and the negative intercept on the vertical R-axis (= internal resistance r)."
            ),
            standardPrecautions = listOf(
                "Ensured all circuit terminal connections and resistance box brass plugs were tight and clean.",
                "Opened the plug key immediately after taking each reading to prevent cell polarization and overheating of the resistor coils.",
                "Noted and corrected for any zero error on the ammeter and voltmeter before connecting the circuit.",
                "Avoided parallax error by aligning the pointer with its reflection in the anti-parallax mirror scale."
            ),
            shortAnswerQuestions = listOf(
                "(b)(i) State two precautions taken to ensure accurate electrical measurements. [2 marks]",
                "(b)(ii) Why is a potentiometer preferred to a voltmeter when measuring the true EMF of a cell? [2 marks]",
                "(b)(iii) From your graph, determine the current I when R = 5.0 Ω. [2 marks]"
            ),
            parameter1Label = "Resistance Box R",
            parameter1Min = 1f,
            parameter1Max = 15f,
            parameter1Default = 4f,
            parameter1Unit = "Ω",
            parameter2Label = "Cell Internal Resistance r",
            parameter2Min = 0.5f,
            parameter2Max = 3.0f,
            parameter2Default = 1.2f,
            parameter2Unit = "Ω"
        ),
        PracticalExperimentSpec(
            id = "heat_calorimetry",
            title = "NECO/NERDC Q4: Heat — Specific Heat Capacity by Electrical Calorimetry",
            category = "Heat",
            examBoard = "NECO",
            yearSpecimen = "NECO Senior Secondary Practical Heat Specimen",
            simType = SimulationType.CALORIMETRY_HEAT,
            aim = "To determine the specific heat capacity c_w of water using an electrical immersion heating coil in a lagged copper calorimeter.",
            apparatus = listOf("Lagged copper calorimeter & stirrer", "12V Heating coil", "Mercury thermometer (0–110°C)", "Stopwatch", "DC power supply", "Ammeter & Voltmeter"),
            theoryFormula = "I V t = (m_w c_w + m_c c_c) Δθ  ⇒  Δθ = [IV / (m_w c_w + m_c c_c)] t",
            targetSlope = 0.054,
            slopeUnit = "°C/s",
            xLabel = "Heating Time t (s)",
            yLabel = "Temperature Rise Δθ (°C)",
            procedureSteps = listOf(
                "1. Weigh the empty dry copper calorimeter with stirrer (m_c = 80 g), then add 100 g of water (m_w = 0.100 kg).",
                "2. Record the initial steady temperature θ₀ of the water using the thermometer.",
                "3. Switch on the heater with I = 2.0 A and V = 12.0 V (Power P = 24.0 W) and start the stopwatch simultaneously.",
                "4. Stir gently and record the temperature θ at t = 60, 120, 180, 240, 300, and 360 s.",
                "5. Plot Temperature Rise Δθ = (θ - θ₀) against Heating Time t (s) and deduce c_w from the slope."
            ),
            standardPrecautions = listOf(
                "Stirred the water continuously and gently to ensure uniform temperature distribution throughout the calorimeter.",
                "Lagged the copper calorimeter thoroughly with cotton wool and used a lid to minimize heat loss by radiation and convection.",
                "Ensured the heating coil was completely submerged before switching on the electric current."
            ),
            shortAnswerQuestions = listOf(
                "(b)(i) State two precautions taken to minimize thermal energy losses. [2 marks]",
                "(b)(ii) Why is copper used for the calorimeter instead of glass? [2 marks]"
            ),
            parameter1Label = "Heating Time t",
            parameter1Min = 30f,
            parameter1Max = 360f,
            parameter1Default = 180f,
            parameter1Unit = "s",
            parameter2Label = "Heater Power IV",
            parameter2Min = 12f,
            parameter2Max = 36f,
            parameter2Default = 24f,
            parameter2Unit = "W"
        ),
        PracticalExperimentSpec(
            id = "wave_resonance",
            title = "WAEC/NECO Q5: Waves — Speed of Sound in Air using Resonance Tube",
            category = "Waves",
            examBoard = "WAEC",
            yearSpecimen = "WAEC Practical Wave Specimen",
            simType = SimulationType.RESONANCE_TUBE_WAVES,
            aim = "To determine the velocity of sound v in air at room temperature and the end-correction c of a resonance tube.",
            apparatus = listOf("Glass resonance tube (100 cm, diameter 3.4 cm)", "Set of 6 calibrated tuning forks (256 Hz – 512 Hz)", "Rubber striking pad", "Metre rule", "Water reservoir beaker"),
            theoryFormula = "L₁ + c = v / (4f)  ⇒  L₁ = (v / 4)(1/f) - c",
            targetSlope = 8575.0,
            slopeUnit = "cm·s⁻¹",
            xLabel = "Reciprocal Frequency 1/f (×10⁻³ s)",
            yLabel = "First Resonant Length L₁ (cm)",
            procedureSteps = listOf(
                "1. Fill the resonance tube nearly to the top with water and select the highest frequency tuning fork (f = 512 Hz).",
                "2. Strike the tuning fork on the rubber pad, hold its vibrating prongs 1 cm above the open tube end, and slowly lower the water level until a loud booming resonance sound is heard.",
                "3. Measure and record the length L₁ of the air column from the top of the tube to the water meniscus.",
                "4. Repeat for f = 480, 426.6, 384, 320, and 256 Hz. Tabulate f (Hz), 1/f (s), and L₁ (cm).",
                "5. Plot L₁ (cm) against 1/f (s); determine the velocity of sound v = 4 × slope and end correction c from the negative y-intercept."
            ),
            standardPrecautions = listOf(
                "Struck the tuning forks only on the soft rubber pad and never on the hard laboratory bench.",
                "Read the water level at the bottom of the concave meniscus at eye level to avoid parallax error.",
                "Held the vibrating prongs horizontally about 1 cm above the mouth of the tube without touching the glass."
            ),
            shortAnswerQuestions = listOf(
                "(b)(i) What is meant by the end-correction of a resonance tube? [2 marks]",
                "(b)(ii) How does an increase in room temperature affect the resonant length L₁? [2 marks]"
            ),
            parameter1Label = "Tuning Fork Frequency f",
            parameter1Min = 256f,
            parameter1Max = 512f,
            parameter1Default = 384f,
            parameter1Unit = "Hz",
            parameter2Label = "Air Column Length L",
            parameter2Min = 10f,
            parameter2Max = 40f,
            parameter2Default = 21.3f,
            parameter2Unit = "cm"
        ),
        PracticalExperimentSpec(
            id = "mag_induction",
            title = "NERDC/WAEC Q6: Electromagnetism — Faraday's Law & Transformer Turns Ratio",
            category = "Magnetism",
            examBoard = "NERDC",
            yearSpecimen = "NERDC SS3 Electromagnetism Practical",
            simType = SimulationType.ELECTROMAGNETIC_INDUCTION,
            aim = "To verify Faraday's law of electromagnetic induction and determine the relationship between secondary voltage V_s and number of secondary turns N_s in a step-down/step-up transformer.",
            apparatus = listOf("Laminated soft-iron C-core", "Primary coil (N_p = 400 turns)", "Multi-tap secondary solenoid (100–800 turns)", "Low-voltage AC supply (6.0 V)", "AC Voltmeter", "Bar magnet & Center-zero Galvanometer"),
            theoryFormula = "V_s / V_p = η (N_s / N_p)  ⇒  V_s = (η V_p / N_p) N_s",
            targetSlope = 0.0142,
            slopeUnit = "V/turn",
            xLabel = "Secondary Turns N_s",
            yLabel = "Secondary Voltage V_s (V)",
            procedureSteps = listOf(
                "1. Mount the primary coil (N_p = 400 turns) and tapped secondary coil on the laminated soft-iron core and clamp the yoke.",
                "2. Connect a 6.0 V AC supply to the primary coil and measure the secondary voltage V_s for N_s = 100, 200, 300, 400, 600, and 800 turns.",
                "3. Plot V_s against N_s and deduce the flux coupling efficiency η from the slope."
            ),
            standardPrecautions = listOf(
                "Clamped the laminated soft-iron C-core halves tightly together to minimize magnetic flux leakage.",
                "Switched off the AC supply between tap changes to prevent sparking and heating."
            ),
            shortAnswerQuestions = listOf(
                "(b)(i) State two causes of energy loss in a practical transformer and how each is minimized. [2 marks]",
                "(b)(ii) State Lenz's law and explain how it obeys the principle of conservation of energy. [2 marks]"
            ),
            parameter1Label = "Secondary Turns N_s",
            parameter1Min = 100f,
            parameter1Max = 800f,
            parameter1Default = 400f,
            parameter1Unit = "turns",
            parameter2Label = "Primary AC Voltage V_p",
            parameter2Min = 2f,
            parameter2Max = 12f,
            parameter2Default = 6f,
            parameter2Unit = "V"
        )
    )

    val physicsMissions: List<PhysicsMission> = listOf(
        PhysicsMission(
            id = "mission_g_master",
            title = "Mission 1: The Gravity Calibration of Lagos & Kano",
            badgeTitle = "Newtonian Pendulum Laureate",
            xpReward = 250,
            description = "Complete the WAEC Q1 Simple Pendulum practical with at least 5 readings, plot T² vs L, and determine g within ±3.0% of 9.80 m/s².",
            requiredExperimentId = "mech_pendulum",
            maxAllowedErrorPercent = 3.0
        ),
        PhysicsMission(
            id = "mission_optics_sniper",
            title = "Mission 2: Zero-Parallax Optical Bench Master",
            badgeTitle = "Snell & Gaussian Ray Master",
            xpReward = 300,
            description = "Determine the focal length of the f = 15.0 cm converging lens with < 2.5% experimental error.",
            requiredExperimentId = "opt_convex_lens",
            maxAllowedErrorPercent = 2.5
        ),
        PhysicsMission(
            id = "mission_ohm_null",
            title = "Mission 3: Potentiometer & Cell EMF Inspector",
            badgeTitle = "Faraday-Ohm Circuit Engineer",
            xpReward = 350,
            description = "Deduce the EMF and internal resistance of the dry Leclanché cell with at least 5 readings and < 4.0% error.",
            requiredExperimentId = "elec_potentiometer",
            maxAllowedErrorPercent = 4.0
        ),
        PhysicsMission(
            id = "mission_sound_resonator",
            title = "Mission 4: Acoustic Resonance & Harmonics",
            badgeTitle = "Harmonic Wave Physicist",
            xpReward = 300,
            description = "Measure the speed of sound in air using the Resonance Tube & Tuning Forks with < 3.5% error.",
            requiredExperimentId = "wave_resonance",
            maxAllowedErrorPercent = 3.5
        )
    )

    val blueprintDeliverables: List<BlueprintSection> = listOf(
        BlueprintSection(
            partNumber = 1,
            title = "1. Complete Product Requirements Document (PRD)",
            subtitle = "Vision, Scope, Personas & Success KPIs for Nigeria & Pan-Africa",
            keyHighlights = listOf(
                "Substitutes 100% of physical WAEC/NECO laboratory equipment for rural & underserved SS1–SS3 schools.",
                "5-in-1 Integrated Platform: Virtual 3D Lab + Physics LMS + WAEC/NECO Exam Simulator + AI Tutor + Automated Grader.",
                "Targets <2GB RAM Android phones/tablets, Windows PCs, offline solar-powered school servers."
            ),
            detailedSpecification = """
                • Executive Vision: Eliminate the practical equipment deficit across 25,000+ Nigerian Senior Secondary Schools by delivering a deterministic + stochastic 3D Physics Laboratory aligned 100% with NERDC, WAEC Paper 3, and NECO Practical Alternative A/B.
                • Primary User Personas:
                  1. SS1–SS3 Student (Rural/Semi-Urban): Practices multi-step apparatus setup, records 5–6 readings with realistic instrument tolerances, plots standard WAEC 2mm/1cm grid graphs, and receives instant AI Examiner feedback.
                  2. Physics Teacher / Lab Coordinator: Assigns practicals, monitors WAEC/NECO readiness scores, exports PDF/CSV mark sheets, and runs timed mock examinations.
                  3. Ministry / Principal / Examiner: Tracks school-wide practical mastery across all 10 NERDC themes.
                • Target Learning Outcome KPI: ≥35% improvement in WAEC/NECO Physics Paper 3 mean scores within 1 academic term.
            """.trimIndent()
        ),
        BlueprintSection(
            partNumber = 2,
            title = "2. Complete Curriculum Mapping Report",
            subtitle = "100% Alignment across NERDC (SS1–SS3), WAEC & NECO Syllabi",
            keyHighlights = listOf(
                "Covers all 10 NERDC Senior Secondary Physics modules: Mechanics, Energy, Properties of Matter, Heat, Waves, Light, Electricity, Magnetism, Modern Physics, Communication Physics.",
                "Maps every topic to Learning Objectives, Practical Activities, Virtual 3D Equipment, Simulation Equations, and WAEC Marking Rubrics."
            ),
            detailedSpecification = """
                • Full interactive Curriculum Matrix available in the 'Curriculum & Equipment' tab.
                • Cross-referenced codes: NERDC-PHY-SS1 through SS3, WAEC General Certificate Examination Section I–V & Paper 3, NECO SSCE Internal/External Practicals.
                • Ensures strict alignment with WAEC Chief Examiners' Reports (e.g., 3 s.f. tabulation consistency, composite units in table headers, ≥75% graph grid utilization, large slope triangle).
            """.trimIndent()
        ),
        BlueprintSection(
            partNumber = 3,
            title = "3. Feature Specifications",
            subtitle = "Functional & Non-Functional Requirements across Core Modules",
            keyHighlights = listOf(
                "Interactive 3D/Canvas Physics Workbench with multi-touch rotation, zoom, zero-error calibration, and real-time vector overlays.",
                "WAEC/NECO Exam Mode with 10-digit candidate number allocation, 2h 45m countdown timer, digital answer booklet, and Chief Examiner rubric grading.",
                "Scientific Examination Graph Sheet with 2mm minor / 1cm major grid, least-squares best-fit line, manual point plotting, slope triangle, intercept, and error bars."
            ),
            detailedSpecification = """
                • Module F1 (3D Simulation Engine): 60 FPS deterministic Runge-Kutta (RK4) + Modified Nodal Analysis (MNA) + 2D/3D Snell Ray Tracer + Gaussian uncertainty injector.
                • Module F2 (Scientific Plotter): Linear & Logarithmic axes, auto-scale or manual exam scale, tangent tool for cooling curves, slope Δy/Δx triangle readout.
                • Module F3 (AI Physics Tutor): Bilingual English & Simple English modes, Android TextToSpeech (TTS) voice synthesis, step-by-step Socratic hints, and WAEC precaution grader.
            """.trimIndent()
        ),
        BlueprintSection(
            partNumber = 4,
            title = "4. User Stories & Acceptance Criteria",
            subtitle = "Agile Epics for Students, Teachers, Examiners & Administrators",
            keyHighlights = listOf(
                "Student Epic: 'As an SS3 student in a rural school without a potentiometer, I want to tap a virtual jockey along a 100cm wire and see the galvanometer deflect so I can find the balance point.'",
                "Teacher Epic: 'As a Physics teacher, I want to assign WAEC 2026 Specimen Q1–Q3 to SS3 and automatically grade each student's table, graph slope, and precautions.'",
                "Offline Sync Epic: 'As a solar-powered rural school lab, I want students to work 100% offline and sync attempts when connectivity returns.'"
            ),
            detailedSpecification = """
                • US-01 (Calibration): Student inspects Vernier calipers/ammeter for zero error before recording readings; system awards +2 bonus accuracy marks.
                • US-02 (Graphing): Student plots ≥5 points on WAEC graph sheet, draws line of best fit, and reads slope triangle spanning >50% of axis.
                • US-03 (AI Viva Voce): Student asks AI Lab Assistant why a pendulum bob must oscillate through θ < 10° and hears spoken Simple English explanation.
            """.trimIndent()
        ),
        BlueprintSection(
            partNumber = 5,
            title = "5. UI/UX Design Blueprint",
            subtitle = "Low-Cognitive-Load Laboratory Interface & Adaptive Canonical Layouts",
            keyHighlights = listOf(
                "High-contrast Emerald-Slate Dark Laboratory theme (#071A15 canvas, #00E676 oscilloscope green, #FFD54F brass accent) optimized for sunlit classrooms and OLED battery saving.",
                "Split-pane workbench on tablets/PCs and bottom-sheet instrument drawers on compact phones.",
                "48dp minimum touch targets with precision fine-tune (+/- 0.1 unit) Vernier steppers for small screens."
            ),
            detailedSpecification = """
                • Typography: Space Grotesk (Display/Headers), Plus Jakarta Sans (Instructional Body), JetBrains Mono (Digital Instruments, Tables, Equations).
                • Accessibility: Colorblind-safe vector coding (arrows + dashed/solid patterns), offline Text-to-Speech (TTS) readout, Simple English toggle.
            """.trimIndent()
        ),
        BlueprintSection(
            partNumber = 6,
            title = "6. Database Schema (Cloud Firestore + Offline Persistence + PostgreSQL Enterprise)",
            subtitle = "Multi-Tenant Zero-Trust Data Model & CRDT Sync Schema",
            keyHighlights = listOf(
                "Live Cloud Firestore Collections: /lab_attempts/{attemptId} and /class_assignments/{assignmentId} with strict ABAC rules.",
                "Offline Disk Cache enabled by default on Android client for seamless rural operation.",
                "Enterprise PostgreSQL + MongoDB hybrid schema for national WAEC/NECO analytics."
            ),
            detailedSpecification = """
                • Firestore Collection 1: /lab_attempts/{attemptId}
                  Fields: userId, studentName, candidateNumber, experimentId, experimentTitle, category, examBoard, score, maxScore, slopeCalculated, errorPercentage, aiFeedback, createdAt, updatedAt.
                • Firestore Collection 2: /class_assignments/{assignmentId}
                  Fields: userId, teacherName, classLevel (SS1|SS2|SS3), experimentId, experimentTitle, examBoard, dueDate, targetAccuracyPercent, status (active|closed), createdAt, updatedAt.
            """.trimIndent()
        ),
        BlueprintSection(
            partNumber = 7,
            title = "7. System Architecture Diagrams & Tech Stack",
            subtitle = "Client-Edge-Cloud Hybrid Architecture for Low-Bandwidth Schools",
            keyHighlights = listOf(
                "Android Native Client (Kotlin, Jetpack Compose, Custom 60FPS Physics & Ray-Tracing Canvas Engine, Firebase Firestore Offline Cache, Firebase AI Logic Gemini 3.8 Flash).",
                "Cross-Platform Web/Desktop Tier: Unity 3D / WebGL + React Teacher Portal.",
                "Local School Edge Server: Raspberry Pi 5 / Mini-PC running Dockerized Node.js/.NET + PostgreSQL on solar power, syncing delta packets to Cloud when 2G/3G/Starlink is available."
            ),
            detailedSpecification = """
                [Student Android / Tablet / WebGL Client]
                       │  (100% Local 60Hz Physics Solver + Local Graph Engine + Offline Cache)
                       ▼
                [Local School Solar Edge Box (Wi-Fi LAN)] ◄───► [Teacher Dashboard & Local Grading]
                       │  (Delta-Compressed Sync over intermittent 2G/3G/4G)
                       ▼
                [Cloud Backend: Firebase Firestore + Auth + App Check + Gemini 3.8 Flash AI Tutor]
            """.trimIndent()
        ),
        BlueprintSection(
            partNumber = 8,
            title = "8. 3D Virtual Asset Inventory & LOD Specifications",
            subtitle = "Low-Poly PBR Asset Pipeline for 36 WAEC/NECO Apparatus Items",
            keyHighlights = listOf(
                "36 Interactive 3D Equipment Models across Mechanics (15), Electricity (13), Optics (8), Heat (5), Waves (4), Magnetism (4).",
                "3-Tier Level of Detail (LOD0: 2,500 tris for close-up Vernier/Micrometer inspection; LOD1: 800 tris; LOD2: 250 tris for entry-level 1GB/2GB RAM phones).",
                "Procedural vertex/canvas shaders for water meniscus, ray beams, galvanometer needles, and glowing heater coils."
            ),
            detailedSpecification = """
                • Total Compressed 3D Asset Bundle Size: < 38 MB (glTF 2.0 + Draco mesh compression + ASTC 4x4 textures) so the entire app installs via offline APK share or school Wi-Fi in < 30 seconds.
            """.trimIndent()
        ),
        BlueprintSection(
            partNumber = 9,
            title = "9. WAEC & NECO Practical Simulation Catalogue",
            subtitle = "Complete Specimen & Past Practical Question Bank (1995–2026)",
            keyHighlights = listOf(
                "Mechanics: Simple Pendulum, Compound Pendulum (Metre Rule with holes), Spiral Spring Hooke's Law, Knife-Edge Principle of Moments, Upthrust & Relative Density.",
                "Optics: Convex Lens (1/v vs 1/u, uv vs u+v, two-position conjugate method), Triangular Prism Minimum Deviation, Rectangular Glass Block Snell's Law, Plane Mirror No-Parallax.",
                "Electricity & Heat & Waves: Potentiometer EMF Comparison, Metre Bridge Resistivity, Ammeter-Voltmeter Internal Resistance, Electrical Calorimetry, Resonance Tube."
            ),
            detailedSpecification = """
                • Each simulation injects a deterministic per-candidate seed (derived from the 10-digit WAEC/NECO Candidate Number) so every student in an exam hall gets realistic, slightly distinct apparatus constants (e.g., g = 9.79–9.81 m/s², f = 14.8–15.2 cm, r = 1.1–1.4 Ω) to prevent copying!
            """.trimIndent()
        ),
        BlueprintSection(
            partNumber = 10,
            title = "10. AI Physics Tutor & Automated Assessment Architecture",
            subtitle = "Powered by Firebase AI Logic (Gemini 3.8 Flash) + Deterministic Rubric Grader",
            keyHighlights = listOf(
                "Hybrid Assessment Engine: Deterministic mathematical grading for tables, units, significant figures, and least-squares slope + Gemini 3.8 Flash LLM grading for precautions, sources of error, and conceptual deductions.",
                "Supports Standard English and Simple English with built-in Android Text-to-Speech (TTS) audio playback.",
                "Guardrailed Socratic Prompting: Gives hints during Practice Mode, locks hints during Timed Exam Mode."
            ),
            detailedSpecification = """
                • WAEC 25-Mark Practical Rubric Breakdown:
                  - Observations & Tabulation (8–10 marks): Correct column headers with units, ≥5 readings, consistent d.p./s.f., composite values (T², 1/u, 1/I).
                  - Graph Plotting (8 marks): Labeled axes with units, sensible scale (≥75% sheet coverage), accurate point plotting (±½ small square), line of best fit.
                  - Slope & Deductions (4 marks): Large right-angled triangle, correct gradient substitution, final constant with unit.
                  - Precautions & Short Questions (3–5 marks): 2 valid past-tense precautions + theoretical question.
            """.trimIndent()
        ),
        BlueprintSection(
            partNumber = 11,
            title = "11. Deployment Strategy for Rural Africa",
            subtitle = "Offline-First, Solar Micro-Server & Zero-Data Peer-to-Peer Distribution",
            keyHighlights = listOf(
                "Runs smoothly on $45 entry-level Android devices (Android 7.0+ API 24, 1.5GB RAM, MediaTek/Unisoc GPUs).",
                "3-Tier Connectivity Model: Fully Offline Standalone APK -> Solar Raspberry Pi Classroom Hub (Wi-Fi LAN) -> Cloud Sync when 2G/3G/Edge is available.",
                "Solar Kit Blueprint: 100W Monocrystalline Panel + 50Ah LiFePO4 Battery + 10-Port USB Tablet Charger + Pi 5 Server (< 18W total draw)."
            ),
            detailedSpecification = """
                • Distribution in zero-internet zones via pre-loaded SD cards, Wi-Fi Direct APK sharing, and State Ministry of Education USB provisioning drives.
            """.trimIndent()
        ),
        BlueprintSection(
            partNumber = 12,
            title = "12. Comprehensive Cost Estimation (CAPEX & OPEX)",
            subtitle = "94% Cost Reduction Compared to Brick-and-Mortar Physics Labs",
            keyHighlights = listOf(
                "Traditional Physical Physics Lab Setup & Consumables: ~$18,000 – $35,000 per school.",
                "NaijaPhysLab 3D Digital Lab (Software + Solar Edge Hub + 20 Shared Rugged Tablets): ~$2,200 per school (~$1.50 per student/year).",
                "Cloud & AI OPEX: < $0.08 per student/year using on-device physics + cached Gemini 3.8 Flash calls."
            ),
            detailedSpecification = """
                • Phase 1 MVP & Pilot (100 Schools, 50,000 Students): $185,000 total engineering + curriculum validation + field pilot budget.
                • National Scale (10,000 Schools, 5M Students): Self-sustaining via state EdTech subventions ($1.20/student/yr) and B2C WAEC/NECO exam prep passes.
            """.trimIndent()
        ),
        BlueprintSection(
            partNumber = 13,
            title = "13. 18-Month Development Roadmap",
            subtitle = "4-Phase Execution Plan from Core Engine to Pan-African Scale",
            keyHighlights = listOf(
                "Months 1–4 (Phase 1): Core 3D/Canvas Physics Solver, Scientific Graph Sheet, Top 12 WAEC/NECO Practicals, Offline Android APK.",
                "Months 5–8 (Phase 2): Full 36 Equipment Inventory, AI Tutor (English/Simple English + TTS), Teacher Dashboard & Automated WAEC Grader.",
                "Months 9–12 (Phase 3): Rural Solar Box Pilot in 6 Nigerian Geopolitical Zones (Lagos, Kano, Enugu, Borno, Rivers, Plateau) with NERDC/WAEC assessors.",
                "Months 13–18 (Phase 4): National rollout across Nigeria + expansion to Ghana, Sierra Leone, Liberia, and The Gambia (WASSCE countries)."
            ),
            detailedSpecification = """
                • Includes continuous teacher training workshops in partnership with STAN (Science Teachers Association of Nigeria) and state Ministries of Education.
            """.trimIndent()
        ),
        BlueprintSection(
            partNumber = 14,
            title = "14. Scalability Plan (Serving 5M+ Concurrent Students)",
            subtitle = "99% Edge Compute Offload & Regional Cloud Sharding",
            keyHighlights = listOf(
                "All 60Hz physics calculations, ray tracing, and graph regressions run 100% on the student's client device (zero server CPU load during simulations).",
                "Firestore document writes use delta batching (< 2 KB per completed practical attempt).",
                "Supports 5M+ concurrent candidates during May/June WAEC & June/July NECO peak examination seasons."
            ),
            detailedSpecification = """
                • Content Delivery: Static 3D apparatus bundles cached indefinitely on local device storage and school LAN boxes.
            """.trimIndent()
        ),
        BlueprintSection(
            partNumber = 15,
            title = "15. Sustainability Strategy",
            subtitle = "Public-Private Partnerships, UBEC/TETFund Alignment & Local Maintenance",
            keyHighlights = listOf(
                "Freemium Public School Tier funded by State Ministries of Education, UBEC, World Bank Adolescent Girls Initiative (AGILE), and Telecom CSR (MTN/Airtel Zero-Rated Educational IP).",
                "Zero physical breakage: virtual glass beakers, thermometers, and galvanometers never shatter or burn out.",
                "Open Teacher Authoring SDK allowing Nigerian physics educators to publish new mock practicals."
            ),
            detailedSpecification = """
                • Long-term financial self-sufficiency through state licensing, private school subscriptions, and individual WASSCE/NECO candidate self-study passes.
            """.trimIndent()
        )
    )
}
