ALTER TABLE Rooms ADD COLUMN room_type VARCHAR(255);

-- Step 1: Create the Building first
INSERT INTO buildings (building_name) VALUES ('Academic Block 1');

-- Step 2: Create the Rooms (Linked to building_id = 1, since it's the first building we added)
INSERT INTO Rooms (room_number, capacity, floor_number, building_id, room_type) VALUES
('001 (DSW Office)', 0, 0, 1, 'Staff Room'),
('002 (Sports Room)', 20, 0, 1, 'Classroom'),
('003 (Boys Common Room)', 20, 0, 1, 'Classroom'),
('004 (Bioscience Lab)', 30, 0, 1, 'Lab'),
('005 (Civil Lab 5)', 30, 0, 1, 'Lab'),
('006 (Associate Dean Office)', 0, 0, 1, 'Staff Room'),
('007', 70, 0, 1, 'Classroom'),
('008', 70, 0, 1, 'Classroom'),
('009', 70, 0, 1, 'Classroom'),
('010', 70, 0, 1, 'Classroom'),
('011 (Computer Network Lab)', 30, 0, 1, 'Lab'),
('012 (Civil Lab 8)', 30, 0, 1, 'Lab'),
('013 (Materials Lab)', 30, 0, 1, 'Lab'),
('014', 0, 0, 1, 'Staff Room'),
('015 (Mech Lab)', 30, 0, 1, 'Lab'),
('016 (Mech Lab)', 30, 0, 1, 'Lab'),
('017 (BIONAC Lab)', 30, 0, 1, 'Lab'),
('018', 70, 0, 1, 'Classroom'),
('019', 70, 0, 1, 'Classroom'),
('020', 70, 0, 1, 'Classroom'),
('021 (Associate Dean Office)', 0, 0, 1, 'Staff Room'),
('022', 70, 0, 1, 'Classroom'),
('023', 0, 0, 1, 'Staff Room'),
('024 (Automotive Lab 1)', 30, 0, 1, 'Lab'),
('025 (Automotive Lab 2)', 30, 0, 1, 'Lab'),
('026 (Electrical Lab 2)', 30, 0, 1, 'Lab'),
('027 (Electrical Lab 1)', 30, 0, 1, 'Lab');