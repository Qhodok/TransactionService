package id.co.softwareseni.transactionservice.controller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Permana on 8/1/2017.
 * Software Enginer
 * Access Mobile Indonesia
 * permana@access-mobile.com
 */
@Controller
@RequestMapping("/transactionservice")
public class RestController {
    protected HashMap<String, JSONArray> types = new HashMap<>();
    protected HashMap<Long, JSONObject> transactions = new HashMap<Long, JSONObject>();

    @RequestMapping(value = "/transaction/{transaction_id}", method = RequestMethod.PUT)
    public ResponseEntity<String> putTransaction(
            @PathVariable("transaction_id") long transaction_id,
            @RequestParam(value = "amount", required = true) double amount,
            @RequestParam(value = "type", required = true) String type,
            @RequestParam(value = "parent_id",required = false) String parent_id) throws JSONException {
        if (transactions.containsKey(transaction_id)) {
            return new ResponseEntity<String>("{\"status\":\"FAILED\",\"message\":\"transaction_id is exist\"}", HttpStatus.OK);
        } else {
            JSONObject transaction = new JSONObject();
            transaction.put("amount", amount);
            transaction.put("type", type);
            if(parent_id != null) {
                transaction.put("parent_id", Long.valueOf(parent_id));
            }
            if (!types.containsKey(type)) {
                types.put(type, new JSONArray());
            }
            types.get(type).put(transaction_id);
            transactions.put(transaction_id, transaction);

            return new ResponseEntity<String>("{\"status\":\"OK\"}", HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/transaction/{transaction_id}", method = RequestMethod.GET)
    public ResponseEntity<String> getTransaction(@PathVariable("transaction_id") long transaction_id) {
        if (this.transactions.containsKey(transaction_id)) {
            return new ResponseEntity<String>(this.transactions.get(transaction_id).toString(), HttpStatus.OK);
        } else {
            return new ResponseEntity<String>("{}", HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/types/{type}", method = RequestMethod.GET)
    public ResponseEntity<String> getTypes(@PathVariable("type") String type) {
        if(this.types.containsKey(type)) {
            return new ResponseEntity<String>(this.types.get(type).toString(), HttpStatus.OK);
        }else{
            return new ResponseEntity<String>("[]",HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/sum/{transactionid}", method = RequestMethod.GET)
    public ResponseEntity<String> getSum(@PathVariable("transactionid") long transaction_id) throws JSONException {
        double sum = 0;
        ArrayList<Long> transaction_ids = new ArrayList<>();
        long temp = transaction_id;
        if (transactions.containsKey(transaction_id)) {
            while (!transaction_ids.contains(temp)) {
                sum += transactions.get(transaction_id).getDouble("amount");
                transaction_ids.add(temp);
                if ((transactions.get(transaction_id).has("parent_id")) && transactions.containsKey(transactions.get(transaction_id).getLong("parent_id"))) {
                    temp = transactions.get(transaction_id).getLong("parent_id");
                } else {
                    break;
                }
            }
            return new ResponseEntity<String>("{\"sum\":" + sum + "}", HttpStatus.OK);
        } else {
            return new ResponseEntity<String>("{\"sum\":0}", HttpStatus.OK);
        }
    }

}
