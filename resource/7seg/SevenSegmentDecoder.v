module SevenSegmentDecoder(
	input[3:0] encoded,
	output reg decoded
);
	always @(*) begin
		case (encoded)
			4'd0: decoded = 1;
			4'd1: decoded = 0;
			4'd2: decoded = 1;
			4'd3: decoded = 1;
			4'd4: decoded = 0;
			4'd5: decoded = 1;
			4'd6: decoded = 1;
			4'd7: decoded = 1;
			4'd8: decoded = 1;
			4'd9: decoded = 1;
			4'd10: decoded = 1;
			4'd11: decoded = 0;
			4'd12: decoded = 1;
			4'd13: decoded = 0;
			4'd14: decoded = 1;
			4'd15: decoded = 1;
			default: decoded = 0;
		endcase
	end
endmodule
